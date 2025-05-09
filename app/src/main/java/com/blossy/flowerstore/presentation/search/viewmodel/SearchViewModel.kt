package com.blossy.flowerstore.presentation.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.usecase.product.GetProductsUseCase
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.blossy.flowerstore.data.remote.dto.ProductResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.utils.SearchHistoryManager

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val searchHistoryManager: SearchHistoryManager
) : ViewModel() {

    private var _isInSearchMode = false
    private var _lastSearchQuery = ""

    private val _productsUiState = MutableStateFlow<UiState<ProductResponse>>(UiState.Loading)
    val productsUiState: StateFlow<UiState<ProductResponse>> = _productsUiState

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory

    fun isInSearchMode(): Boolean = _isInSearchMode

    private var currentPage = 1
    private var totalPages = 1
    private var currentKeyword = ""
    private var currentCategories = emptySet<String>()
    private var currentMinPrice: Int? = null
    private var currentMaxPrice: Int? = null

    fun searchProducts(
        keyword: String,
        categories: Set<String> = emptySet(),
        minPrice: Int? = null,
        maxPrice: Int? = null,
        loadMore: Boolean = false
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (!loadMore) {
                    currentPage = 1
                    currentKeyword = keyword
                    currentCategories = categories
                    currentMinPrice = minPrice
                    currentMaxPrice = maxPrice
                    _productsUiState.value = UiState.Loading
                } else if (currentPage >= totalPages) {
                    return@launch
                }
                val result = getProductsUseCase.invoke(
                    keyword = keyword,
                    categories = categories,
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    page = currentPage
                )

                when (result) {
                    is Result.Success -> {
                        totalPages = result.data.pagination.pages
                        if (loadMore) {
                            val currentData = (_productsUiState.value as? UiState.Success)?.data
                            if (currentData != null) {
                                val mergedProducts = currentData.products + result.data.products
                                val mergedData = result.data.copy(
                                    products = mergedProducts,
                                    count = mergedProducts.size
                                )
                                _productsUiState.value = UiState.Success(mergedData)
                            }
                        } else {
                            _productsUiState.value = UiState.Success(result.data)
                        }
                        currentPage++
                    }
                    is Result.Error -> {
                        _productsUiState.value = UiState.Error(result.message ?: "Unknown error")
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                _productsUiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadMoreProducts() {
        if (currentPage <= totalPages) {
            searchProducts(
                keyword = currentKeyword,
                categories = currentCategories,
                minPrice = currentMinPrice,
                maxPrice = currentMaxPrice,
                loadMore = true
            )
        }
    }

    fun loadSearchHistory() {
        _searchHistory.value = searchHistoryManager.getHistory()
    }

    fun setSearchMode(isSearching: Boolean) {
        _isInSearchMode = isSearching
    }

    fun getLastSearchQuery(): String = _lastSearchQuery

    fun setLastSearchQuery(query: String) {
        _lastSearchQuery = query
    }


    fun clearSearchHistory() {
        searchHistoryManager.clearHistory()
        _searchHistory.value = emptyList()
    }
}