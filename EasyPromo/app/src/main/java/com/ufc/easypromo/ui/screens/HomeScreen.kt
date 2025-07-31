package com.ufc.easypromo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ufc.easypromo.models.Product
import com.ufc.easypromo.ui.components.ProductCard
import com.ufc.easypromo.ui.components.PromotionSource
import com.ufc.easypromo.viewmodel.ProductViewModel

@Composable
fun HomeScreen(
    source: PromotionSource,
    onProductClick: (Product) -> Unit,
    productViewModel: ProductViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    val isLoading by productViewModel.isLoading.collectAsState(initial = false)
    val favoriteIds by productViewModel.favoriteIds.collectAsState(initial = emptySet())
    val cartIds by productViewModel.cartIds.collectAsState(initial = emptySet())

    LaunchedEffect(source, searchQuery) {
        productViewModel.loadProducts(source, searchQuery) { loaded ->
            products = loaded
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                placeholder = { Text("Buscar produtos...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                singleLine = true
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(products) { product ->
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
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}