package com.ufc.easypromo.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ufc.easypromo.models.Product
import com.ufc.easypromo.ui.components.BottomNavigationBar
import com.ufc.easypromo.ui.components.DrawerContent
import com.ufc.easypromo.ui.components.PromotionSource
import com.ufc.easypromo.ui.components.TopBar
import com.ufc.easypromo.ui.screens.CartScreen
import com.ufc.easypromo.ui.screens.ConfigurationScreen
import com.ufc.easypromo.ui.screens.FavouritesScreen
import com.ufc.easypromo.ui.screens.HelpScreen
import com.ufc.easypromo.ui.screens.HomeScreen
import com.ufc.easypromo.ui.screens.ProductDetailScreen
import kotlinx.coroutines.launch

enum class Screen { Home, Favourites, Cart, ProductDetails, Configuration, Help }

@Composable
fun AppNavHost(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    var currentScreen by remember { mutableStateOf(Screen.Home) }
    var selectedSource by remember { mutableStateOf(PromotionSource.Amazon) }
    val drawerState = androidx.compose.material3.rememberDrawerState(
        androidx.compose.material3.DrawerValue.Closed
    )
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onProductClick = {
                    selectedProduct = it
                    currentScreen = Screen.ProductDetails
                    scope.launch { drawerState.close() }
                },
                onFavouritesClick = {
                    currentScreen = Screen.Favourites
                    scope.launch { drawerState.close() }
                },
                onConfigClick = {
                    currentScreen = Screen.Configuration
                    scope.launch { drawerState.close() }
                },
                onHelpClick = {
                    currentScreen = Screen.Help
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onCartClick = { currentScreen = Screen.Cart }
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    selectedSource = selectedSource,
                    onSourceSelected = {
                        selectedSource = it
                        currentScreen = Screen.Home
                    }
                )
            }
        ) { innerPadding ->
            when (currentScreen) {
                Screen.Home -> HomeScreen(
                    source = selectedSource,
                    onProductClick = {
                        selectedProduct = it
                        currentScreen = Screen.ProductDetails
                    },
                    modifier = Modifier.padding(innerPadding)
                )
                Screen.Favourites -> FavouritesScreen(
                    onProductClick = {
                        selectedProduct = it
                        currentScreen = Screen.ProductDetails
                    },
                    modifier = Modifier.padding(innerPadding)
                )
                Screen.Cart -> CartScreen(
                    onProductClick = {
                        selectedProduct = it
                        currentScreen = Screen.ProductDetails
                    },
                    modifier = Modifier.padding(innerPadding)
                )
                Screen.ProductDetails -> selectedProduct?.let { product ->
                    ProductDetailScreen(
                        product = product,
                        onBack = { currentScreen = Screen.Home },
                        onProductClick = { newProduct ->
                            selectedProduct = newProduct
                            // Stay on ProductDetails
                        }
                    )
                } ?: HomeScreen(
                    source = selectedSource,
                    onProductClick = {
                        selectedProduct = it
                        currentScreen = Screen.ProductDetails
                    },
                    modifier = Modifier.padding(innerPadding)
                )
                Screen.Configuration -> ConfigurationScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeChange = onThemeChange,
                    onBack = { currentScreen = Screen.Home }
                )
                Screen.Help -> HelpScreen(
                    onBack = { currentScreen = Screen.Home }
                )
            }
        }
    }
}