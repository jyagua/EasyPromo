package com.ufc.easypromo.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ufc.easypromo.data.PreferencesDataStore.Keys.CART_IDS
import com.ufc.easypromo.data.PreferencesDataStore.Keys.FAVORITE_IDS
import com.ufc.easypromo.models.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_prefs")

class PreferencesDataStore(private val context: Context) {

    private object Keys {
        val FAVORITE_IDS = stringSetPreferencesKey("favorite_ids")
        val CART_IDS = stringSetPreferencesKey("cart_ids")
        val PRICE_DROP_NOTIFICATIONS = booleanPreferencesKey("price_drop_notifications")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
    }

    // Expose flows of just the IDs. The ViewModel will handle fetching the product details.
    val favoriteIds: Flow<Set<String>> = context.dataStore.data
        .map { prefs -> prefs[FAVORITE_IDS] ?: emptySet() }

    val cartIds: Flow<Set<String>> = context.dataStore.data
        .map { prefs -> prefs[CART_IDS] ?: emptySet() }

    val priceDropEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.PRICE_DROP_NOTIFICATIONS] == true }
    val darkThemeEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.DARK_THEME] != false }

    suspend fun toggleFavorite(product: Product) {
        context.dataStore.edit { prefs ->
            val currentFavorites = (prefs[FAVORITE_IDS] ?: emptySet()).toMutableSet()
            val productIdStr = product.id.toString()
            if (currentFavorites.contains(productIdStr)) {
                currentFavorites.remove(productIdStr)
            } else {
                currentFavorites.add(productIdStr)
            }
            prefs[FAVORITE_IDS] = currentFavorites
        }
    }

    suspend fun toggleInCart(product: Product) {
        context.dataStore.edit { prefs ->
            val currentCart = (prefs[CART_IDS] ?: emptySet()).toMutableSet()
            val productIdStr = product.id.toString()
            if (currentCart.contains(productIdStr)) {
                currentCart.remove(productIdStr)
            } else {
                currentCart.add(productIdStr)
            }
            prefs[CART_IDS] = currentCart
        }
    }

    suspend fun setPriceDropEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.PRICE_DROP_NOTIFICATIONS] = enabled
        }
    }

    suspend fun setDarkThemeEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DARK_THEME] = enabled
        }
    }

    suspend fun overrideFavorites(ids: List<Long>) {
        context.dataStore.edit { prefs ->
            prefs[FAVORITE_IDS] = ids.map { it.toString() }.toSet()
        }
    }

    suspend fun overrideCart(ids: List<Long>) {
        context.dataStore.edit { prefs ->
            prefs[CART_IDS] = ids.map { it.toString() }.toSet()
        }
    }

    suspend fun clearFavorites() {
        context.dataStore.edit { prefs ->
            prefs[FAVORITE_IDS] = emptySet()
        }
    }

    suspend fun clearCart() {
        context.dataStore.edit { prefs ->
            prefs[CART_IDS] = emptySet()
        }
    }

}