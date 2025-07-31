package com.ufc.easypromo.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ufc.easypromo.models.Product
import com.ufc.easypromo.ui.components.ProductCard
import com.ufc.easypromo.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onProductClick: (Product) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    productViewModel: ProductViewModel
) {
    val cartItems by productViewModel.cartItems.collectAsState(initial = emptyList())
    val favoriteIds by productViewModel.favoriteIds.collectAsState(initial = emptySet())
    val cartIds by productViewModel.cartIds.collectAsState(initial = emptySet())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrinho") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (cartItems.isEmpty()) {
                item {
                    Text("Seu carrinho está vazio.")
                }
            } else {
                items(cartItems) { product ->
                    ProductCard(
                        product = product,
                        isFavorite = favoriteIds.contains(product.id.toString()),
                        isInCart = cartIds.contains(product.id.toString()),
                        onProductClick = onProductClick,
                        onToggleFavorite = { productViewModel.toggleFavorite(product) },
                        onToggleCart = { productViewModel.toggleInCart(product) }
                    )
                }
            }
        }
    }
}