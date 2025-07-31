package com.ufc.easypromo.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.ufc.easypromo.aliexpress.ui.AliExpressScreen
import com.ufc.easypromo.aliexpress.viewmodel.AliExpressViewModel
import com.ufc.easypromo.di.AppContainer
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
import com.ufc.easypromo.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    productViewModel: ProductViewModel,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedSource by remember { mutableStateOf(PromotionSource.Amazon) }

    viewModel(
        factory = remember { AppContainer().aliExpressViewModelFactory }
    )

    val favoriteProducts by productViewModel.favourites.collectAsState(initial = emptyList())

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            DrawerContent(
                favoriteProducts = favoriteProducts,
                onProductClick = { product ->
                    scope.launch { drawerState.close() }
                    val productJson = Gson().toJson(product)
                    navController.navigate("details/${java.net.URLEncoder.encode(productJson, "UTF-8")}")
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
        }
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
                        val route = if (source == PromotionSource.Amazon) "home" else "aliexpress"
                        navController.navigate(route) {
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
                            val productJson = Gson().toJson(product)
                            navController.navigate("details/${java.net.URLEncoder.encode(productJson, "UTF-8")}")
                        },
                        productViewModel = productViewModel
                    )
                }
                composable("aliexpress") {
                    AliExpressScreen(
                        viewModelFactory = AppContainer().aliExpressViewModelFactory,
                        onProductClick = { product ->
                            val productJson = Gson().toJson(product)
                            navController.navigate("details/${java.net.URLEncoder.encode(productJson, "UTF-8")}")
                        },
                        productViewModel = productViewModel
                    )
                }
                composable("favourites") {
                    FavouritesScreen(
                        onProductClick = { product ->
                            val productJson = Gson().toJson(product)
                            navController.navigate("details/${java.net.URLEncoder.encode(productJson, "UTF-8")}")
                        },
                        onBack = { navController.popBackStack() },
                        productViewModel = productViewModel
                    )
                }
                composable("cart") {
                    CartScreen(
                        onProductClick = { product ->
                            val productJson = Gson().toJson(product)
                            navController.navigate("details/${java.net.URLEncoder.encode(productJson, "UTF-8")}")
                        },
                        onBack = { navController.popBackStack() },
                        productViewModel = productViewModel
                    )
                }
                composable(
                    "details/{productJson}",
                    arguments = listOf(navArgument("productJson") { type = NavType.StringType })
                ) { backStackEntry ->
                    val productJson = backStackEntry.arguments?.getString("productJson")
                    val product = Gson().fromJson(java.net.URLDecoder.decode(productJson, "UTF-8"), Product::class.java)
                    if (product != null) {
                        ProductDetailScreen(
                            product = product,
                            onBack = { navController.popBackStack() },
                            onProductClick = { newProduct ->
                                val newProductJson = Gson().toJson(newProduct)
                                navController.navigate("details/${java.net.URLEncoder.encode(newProductJson, "UTF-8")}")
                            },
                            productViewModel = productViewModel
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