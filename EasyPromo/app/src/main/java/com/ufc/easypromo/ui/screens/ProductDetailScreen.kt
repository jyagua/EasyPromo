package com.ufc.easypromo.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ufc.easypromo.models.Product
import com.ufc.easypromo.ui.components.ProductCard
import com.ufc.easypromo.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    onBack: () -> Unit,
    onProductClick: (Product) -> Unit,
    productViewModel: ProductViewModel
) {
    val favoriteIds by productViewModel.favoriteIds.collectAsState(initial = emptySet())
    val cartIds by productViewModel.cartIds.collectAsState(initial = emptySet())
    val recommendedProducts by productViewModel.recommendedProducts.collectAsState(initial = emptyList())
    val isLoadingRecommendations by productViewModel.isLoading.collectAsState(initial = false)
    val isAliExpressProduct = product.store == "AliExpress"
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(product.id) {
        if (isAliExpressProduct) {
            productViewModel.loadRecommendedProducts(product.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.name, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(24.dp)
        ) {
            item {
                if (product.imageUrl != null) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = product.imageRes),
                        contentDescription = product.name,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Price and Discount
                if (product.discountPercent > 1) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "R$ %.2f".format(product.price),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "R$ %.2f".format(product.originalPrice),
                            style = MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.LineThrough),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "${product.discountPercent.toInt()}% OFF",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    Text(
                        text = "R$ %.2f".format(product.price),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Categoria: ${product.category}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons (Favorite and Cart)
                Row {
                    val isFav = favoriteIds.contains(product.id.toString())
                    val favScale by animateFloatAsState(targetValue = if (isFav) 1.3f else 1f, label = "")
                    IconButton(onClick = { productViewModel.toggleFavorite(product) }) {
                        Icon(
                            imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favoritar",
                            modifier = Modifier.scale(favScale)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    val isInCart = cartIds.contains(product.id.toString())
                    val cartScale by animateFloatAsState(targetValue = if (isInCart) 1.3f else 1f, label = "")
                    IconButton(onClick = { productViewModel.toggleInCart(product) }) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Adicionar ao carrinho",
                            modifier = Modifier.scale(cartScale)
                        )
                    }
                }

                if (isAliExpressProduct) {
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    product.promotionLink?.let { link ->
                        val annotatedLink = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)) {
                                append("Abrir no AliExpress")
                            }
                        }
                        ClickableText(
                            text = annotatedLink,
                            onClick = { uriHandler.openUri(link) },
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Recommended Products Section
            if (isLoadingRecommendations) {
                item {
                    CircularProgressIndicator(modifier = Modifier.padding(vertical = 32.dp))
                }
            } else if (recommendedProducts.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        "Produtos Recomendados",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(recommendedProducts) { recommendedProduct ->
                    ProductCard(
                        product = recommendedProduct,
                        isFavorite = favoriteIds.contains(recommendedProduct.id.toString()),
                        isInCart = cartIds.contains(recommendedProduct.id.toString()),
                        onProductClick = onProductClick,
                        onToggleFavorite = { productViewModel.toggleFavorite(it) },
                        onToggleCart = { productViewModel.toggleInCart(it) }
                    )
                }
            }
        }
    }
}