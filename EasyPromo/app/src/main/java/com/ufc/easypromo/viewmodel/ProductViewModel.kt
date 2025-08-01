package com.ufc.easypromo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ufc.easypromo.data.PreferencesDataStore
import com.ufc.easypromo.di.AppContainer
import com.ufc.easypromo.models.Product
import com.ufc.easypromo.models.productList
import com.ufc.easypromo.ui.components.PromotionSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val preferencesDataStore = PreferencesDataStore(application)
    private val aliExpressRepository = AppContainer().aliExpressRepository

    // A central map to store all products from all sources by their ID.
    private val _allProducts = MutableStateFlow<Map<Long, Product>>(emptyMap())

    // Directly expose the ID sets from the DataStore.
    val favoriteIds: Flow<Set<String>> = preferencesDataStore.favoriteIds
    val cartIds: Flow<Set<String>> = preferencesDataStore.cartIds

    // Create the full favorite and cart product lists by combining the IDs with the central product map.
    val favourites: Flow<List<Product>> = combine(favoriteIds, _allProducts) { ids, productsMap ->
        ids.mapNotNull { idString -> productsMap[idString.toLong()] }
    }
    val cartItems: Flow<List<Product>> = combine(cartIds, _allProducts) { ids, productsMap ->
        ids.mapNotNull { idString -> productsMap[idString.toLong()] }
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _recommendedProducts = MutableStateFlow<List<Product>>(emptyList())
    val recommendedProducts: StateFlow<List<Product>> = _recommendedProducts

    /**
     * Adds a list of products to the central map, ensuring the app knows about them.
     */
    fun addProducts(products: List<Product>) {
        val currentProducts = _allProducts.value.toMutableMap()
        products.forEach { product ->
            currentProducts[product.id] = product
        }
        _allProducts.value = currentProducts
    }

    /**
     * Finds a product in the central map by its ID.
     */
    fun getProductById(productId: Long): Product? {
        return _allProducts.value[productId]
    }

    fun loadProducts(source: PromotionSource, query: String, onLoaded: (List<Product>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            // This part simulates loading from a local source like "Amazon".
            val products = productList.filter { it.store == source.label }
                .filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true)
                }
            delay(200)
            addProducts(products) // Add the loaded products to our central store.
            onLoaded(products)
            _isLoading.value = false
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch { preferencesDataStore.toggleFavorite(product) }
    }

    fun toggleInCart(product: Product) {
        viewModelScope.launch { preferencesDataStore.toggleInCart(product) }
    }

    fun loadRecommendedProducts(productId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _recommendedProducts.value = emptyList()
            try {
                val recommended = aliExpressRepository.getSmartMatchProducts(productId)
                addProducts(recommended) // Also add recommended products to the central store.
                _recommendedProducts.value = recommended
            } catch (e: Exception) {
                // Handle error appropriately
            }
            _isLoading.value = false
        }
    }

    fun clearFavorites() {
        viewModelScope.launch { preferencesDataStore.clearFavorites() }
    }
}