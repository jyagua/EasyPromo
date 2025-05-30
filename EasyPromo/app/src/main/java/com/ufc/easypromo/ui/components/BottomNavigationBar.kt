package com.ufc.easypromo.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

enum class PromotionSource(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Amazon("Amazon", Icons.Filled.Store),
    AliExpress("AliExpress", Icons.Filled.LocalMall)
}

@Composable
fun BottomNavigationBar(
    selectedSource: PromotionSource,
    onSourceSelected: (PromotionSource) -> Unit
) {
    NavigationBar {
        PromotionSource.values().forEach { source ->
            NavigationBarItem(
                icon = { Icon(source.icon, contentDescription = source.label) },
                label = { Text(source.label) },
                selected = selectedSource == source,
                onClick = { onSourceSelected(source) }
            )
        }
    }
}