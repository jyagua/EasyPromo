package com.ufc.easypromo.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ufc.easypromo.models.productList
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedSource by remember { mutableStateOf(PromotionSource.Amazon) }

    ModalNavigationDrawer(
        drawerContent = {
            DrawerContent(
                onProductClick = { product ->
                    scope.launch { drawerState.close() }
                    navController.navigate("details/${product.id}")
                },
                onFavouritesClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("favourites")
                },
                onConfigClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("config")
                },
                onHelpClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("help")
                }
            )
        },
        drawerState = drawerState,
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onCartClick = { navController.navigate("cart") }
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    selectedSource = selectedSource,
                    onSourceSelected = { source ->
                        selectedSource = source
                        navController.navigate("home") {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    HomeScreen(
                        source = selectedSource,
                        onProductClick = { product ->
                            navController.navigate("details/${product.id}")
                        }
                    )
                }
                composable("favourites") {
                    FavouritesScreen(
                        onProductClick = { product ->
                            navController.navigate("details/${product.id}")
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("cart") {
                    CartScreen(
                        onProductClick = { product ->
                            navController.navigate("details/${product.id}")
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    "details/{productId}",
                    arguments = listOf(navArgument("productId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val productId = backStackEntry.arguments?.getInt("productId")
                    val product = productList.find { it.id == productId }
                    if (product != null) {
                        ProductDetailScreen(
                            product = product,
                            onBack = { navController.popBackStack() },
                            onProductClick = { newProduct ->
                                navController.navigate("details/${newProduct.id}")
                            }
                        )
                    }
                }
                composable("config") {
                    ConfigurationScreen(
                        isDarkTheme = isDarkTheme,
                        onThemeChange = onThemeChange,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("help") {
                    HelpScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}