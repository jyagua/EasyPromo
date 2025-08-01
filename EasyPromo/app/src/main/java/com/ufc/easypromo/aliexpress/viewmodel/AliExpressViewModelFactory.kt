package com.ufc.easypromo.aliexpress.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ufc.easypromo.aliexpress.data.repository.AliExpressRepository

/**
 * Factory for creating AliExpressViewModel instances with a repository dependency.
 */
class AliExpressViewModelFactory(
    private val repository: AliExpressRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AliExpressViewModel::class.java)) {
            return AliExpressViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}