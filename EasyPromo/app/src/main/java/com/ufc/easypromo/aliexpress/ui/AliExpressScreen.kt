package com.ufc.easypromo.aliexpress.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ufc.easypromo.aliexpress.viewmodel.AliExpressViewModel
import com.ufc.easypromo.aliexpress.viewmodel.AliExpressViewModelFactory
import com.ufc.easypromo.models.Product
import com.ufc.easypromo.ui.components.ProductCard
import com.ufc.easypromo.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AliExpressScreen(
    viewModelFactory: AliExpressViewModelFactory,
    onProductClick: (Product) -> Unit,
    productViewModel: ProductViewModel
) {
    val viewModel: AliExpressViewModel = viewModel(factory = viewModelFactory)
    val products by viewModel.products.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val isLoadingMore by viewModel.isLoadingMore.observeAsState(false)
    val error by viewModel.error.observeAsState(null)
    val searchQuery by viewModel.searchQuery.observeAsState("")
    val sortOrder by viewModel.sortOrder.observeAsState("")

    val favoriteIds by productViewModel.favoriteIds.collectAsState(initial = emptySet())
    val cartIds by productViewModel.cartIds.collectAsState(initial = emptySet())
    val keyboardController = LocalSoftwareKeyboardController.current

    // When the list of products from the API changes, add them to the central ViewModel.
    LaunchedEffect(products) {
        if (products.isNotEmpty()) {
            productViewModel.addProducts(products)
        }
    }

    val sortOptions = mapOf(
        "Melhor Resultado" to "",
        "Maior Desconto" to "DISCOUNT_DESC",
        "Mais Vendidos" to "LAST_VOLUME_DESC",
        "Preço: Menor para Maior" to "SALE_PRICE_ASC",
        "Preço: Maior para Menor" to "SALE_PRICE_DESC"
    )
    val currentSortLabel = sortOptions.entries.find { it.value == sortOrder }?.key ?: "Ordenar por"
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpar busca")
                    }
                }
            },
            placeholder = { Text("Buscar no AliExpress...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.fetchProducts(forceRefresh = true)
                keyboardController?.hide()
            })
        )

        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
        ) {
            OutlinedTextField(
                value = currentSortLabel,
                onValueChange = {},
                readOnly = true,
                label = { Text("Ordenar por") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                sortOptions.forEach { (label, value) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            viewModel.onSortOrderChanged(value)
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading == true && products.isEmpty()) {
                CircularProgressIndicator()
            } else if (error != null && products.isEmpty()) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error)
            } else {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing = isLoading ?: false),
                    onRefresh = { viewModel.fetchProducts(forceRefresh = true) },
                ) {
                    LazyColumn(modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)) {
                        itemsIndexed(products) { index, product ->
                            if (index == products.size - 1 && isLoadingMore == false) {
                                LaunchedEffect(Unit) {
                                    viewModel.fetchProducts()
                                }
                            }

                            ProductCard(
                                product = product,
                                isFavorite = favoriteIds.contains(product.id.toString()),
                                isInCart = cartIds.contains(product.id.toString()),
                                onProductClick = onProductClick,
                                onToggleFavorite = { productViewModel.toggleFavorite(product) },
                                onToggleCart = { productViewModel.toggleInCart(product) }
                            )
                        }

                        if (isLoadingMore == true) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}