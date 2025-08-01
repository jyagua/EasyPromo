package com.ufc.easypromo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ufc.easypromo.models.Product

@Composable
fun DrawerContent(
    favoriteProducts: List<Product>,
    onProductClick: (Product) -> Unit,
    onFavouritesClick: () -> Unit,
    onConfigClick: () -> Unit,
    onHelpClick: () -> Unit,
    /*onLoginClick: () -> Unit*/
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp)
            .background(MaterialTheme.colorScheme.surface),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "EasyPromo",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Favoritos",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFavouritesClick() }
                    .padding(vertical = 8.dp)
            )

            // Display a few favorite products as quick links
            favoriteProducts.take(3).forEach { product ->
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onProductClick(product) }
                        .padding(start = 12.dp, top = 4.dp, bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Configurações",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onConfigClick() }
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ajuda",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onHelpClick() }
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            /*Text(
                text = "Login / Registrar",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLoginClick() }
                    .padding(vertical = 8.dp)
            )*/

            HorizontalDivider()
            Text(
                text = "Versão 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}