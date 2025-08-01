package com.ufc.easypromo.aliexpress.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ufc.easypromo.aliexpress.data.repository.AliExpressRepository
import com.ufc.easypromo.models.Product
import kotlinx.coroutines.launch

class AliExpressViewModel(private val repository: AliExpressRepository) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> = _products

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoadingMore = MutableLiveData(false)
    val isLoadingMore: LiveData<Boolean> = _isLoadingMore

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _sortOrder = MutableLiveData("")
    val sortOrder: LiveData<String> = _sortOrder

    private var currentPage = 1
    private var isFetching = false
    private var currentKeywords = ""
    private var currentSortOrder = ""

    init {
        fetchProducts(forceRefresh = true)
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSortOrderChanged(newSortOrder: String) {
        _sortOrder.value = newSortOrder
        fetchProducts(forceRefresh = true)
    }

    fun fetchProducts(forceRefresh: Boolean = false) {
        val keywords = _searchQuery.value ?: ""
        val sort = _sortOrder.value ?: ""

        val isNewSearch = keywords != currentKeywords
        val isNewSort = sort != currentSortOrder
        val isRefresh = forceRefresh || isNewSearch || isNewSort

        if (isRefresh) {
            currentPage = 1
            currentKeywords = keywords
            currentSortOrder = sort
        }

        if (isFetching) return

        viewModelScope.launch {
            isFetching = true
            if (currentPage == 1) _isLoading.value = true else _isLoadingMore.value = true
            _error.value = null

            if (isRefresh) {
                _products.postValue(emptyList())
            }

            try {
                val (newProducts, _) = repository.getProducts(currentPage, keywords, sort)
                val currentList = if (isRefresh) emptyList() else _products.value ?: emptyList()

                val sortedList = if (sort == "DISCOUNT_DESC") {
                    newProducts.sortedByDescending { it.discountPercent }
                } else {
                    newProducts
                }

                _products.postValue(currentList + sortedList)

                if (newProducts.isNotEmpty()) {
                    currentPage++
                }
            } catch (e: Exception) {
                _error.postValue("Failed to load products: ${e.message}")
            } finally {
                if (currentPage == 1 || isRefresh) _isLoading.value = false else _isLoadingMore.value = false
                isFetching = false
            }
        }
    }
}