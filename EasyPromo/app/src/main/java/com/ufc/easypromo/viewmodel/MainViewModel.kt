package com.ufc.easypromo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ufc.easypromo.data.PreferencesDataStore
import com.ufc.easypromo.models.checkPriceDropsAndNotify
import com.ufc.easypromo.models.productList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val preferencesDataStore = PreferencesDataStore(application)

    val darkThemeEnabled = preferencesDataStore.darkThemeEnabled
    val priceDropEnabled = preferencesDataStore.priceDropEnabled

    fun setDarkThemeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setDarkThemeEnabled(enabled)
        }
    }

    fun setPriceDropEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setPriceDropEnabled(enabled)
        }
    }

    fun checkPriceDropsAndNotify() {
        viewModelScope.launch {
            val enabled = preferencesDataStore.priceDropEnabled.first()
            checkPriceDropsAndNotify(getApplication(), productList, enabled)
        }
    }
}