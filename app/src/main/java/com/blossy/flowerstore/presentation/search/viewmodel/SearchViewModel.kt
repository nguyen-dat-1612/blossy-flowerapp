package com.blossy.flowerstore.presentation.search.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.usecase.product.GetProductsUseCase
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.blossy.flowerstore.domain.model.response.ProductListModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.presentation.search.state.SearchUiState
import com.blossy.flowerstore.utils.manager.SearchHistoryManager
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val searchHistoryManager: SearchHistoryManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    val isInSearchMode: Boolean get() = _uiState.value.isInSearchMode
    val lastSearchQuery: String get() = _uiState.value.lastSearchQuery

    init {
        loadSearchHistory()
    }

    fun searchProducts(
        keyword: String,
        categories: Set<String> = emptySet(),
        minPrice: Int? = null,
        maxPrice: Int? = null,
        loadMore: Boolean = false
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentState = _uiState.value

                if (!loadMore) {
                    // First search - reset everything
                    _uiState.value = currentState.copy(
                        currentPage = 1,
                        currentKeyword = keyword,
                        currentCategories = categories,
                        currentMinPrice = minPrice,
                        currentMaxPrice = maxPrice,
                        products = UiState.Loading,
                        isLoading = true,
                        canLoadMore = false
                    )
                    Log.d("SearchViewModel", "Starting new search: keyword='$keyword', page=1")
                } else {
                    // Load more - check if we can load more
                    if (currentState.currentPage >= currentState.totalPages) {
                        Log.d("SearchViewModel", "Cannot load more: currentPage=${currentState.currentPage}, totalPages=${currentState.totalPages}")
                        return@launch
                    }
                    _uiState.value = currentState.copy(isLoading = true)
                    Log.d("SearchViewModel", "Loading more: page=${currentState.currentPage}")
                }

                val pageToLoad = if (loadMore) currentState.currentPage else 1

                val result = getProductsUseCase.invoke(
                    keyword = keyword,
                    categories = categories,
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    page = pageToLoad
                )

                when (result) {
                    is Result.Success -> {
                        val newTotalPages = result.data.pagination.pages
                        val nextPage = pageToLoad + 1
                        val canLoadMore = nextPage <= newTotalPages

                        Log.d("SearchViewModel", "API Success: page=$pageToLoad, totalPages=$newTotalPages, " +
                                "receivedProducts=${result.data.products.size}, totalCount=${result.data.count}")

                        if (loadMore) {
                            // Merge with existing products
                            val currentData = (currentState.products as? UiState.Success)?.data
                            if (currentData != null) {
                                val mergedProducts = currentData.products + result.data.products
                                val mergedData = result.data.copy(
                                    products = mergedProducts,
                                    count = mergedProducts.size
                                )

                                Log.d("SearchViewModel", "Merged products: total=${mergedProducts.size}")

                                _uiState.value = currentState.copy(
                                    products = UiState.Success(mergedData),
                                    currentPage = nextPage,
                                    totalPages = newTotalPages,
                                    canLoadMore = canLoadMore,
                                    isLoading = false
                                )
                            } else {
                                Log.e("SearchViewModel", "Cannot merge: current data is null")
                                _uiState.value = currentState.copy(isLoading = false)
                            }
                        } else {
                            // First load
                            _uiState.value = currentState.copy(
                                products = UiState.Success(result.data),
                                currentPage = nextPage,
                                totalPages = newTotalPages,
                                canLoadMore = canLoadMore,
                                isLoading = false
                            )
                        }
                    }
                    is Result.Error -> {
                        Log.e("SearchViewModel", "API Error: ${result.message}")
                        _uiState.value = currentState.copy(
                            products = UiState.Error(result.message ?: "Unknown error"),
                            isLoading = false
                        )
                    }
                    else -> {
                        Log.w("SearchViewModel", "API returned unexpected result type")
                        _uiState.value = currentState.copy(isLoading = false)
                    }
                }
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Search exception: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    products = UiState.Error(e.message ?: "Unknown error"),
                    isLoading = false
                )
            }
        }
    }

    fun loadMoreProducts() {
        val currentState = _uiState.value

        Log.d("SearchViewModel", "loadMoreProducts called: canLoadMore=${currentState.canLoadMore}, " +
                "isLoading=${currentState.isLoading}, currentPage=${currentState.currentPage}, " +
                "totalPages=${currentState.totalPages}")

        if (currentState.canLoadMore && !currentState.isLoading) {
            searchProducts(
                keyword = currentState.currentKeyword,
                categories = currentState.currentCategories,
                minPrice = currentState.currentMinPrice,
                maxPrice = currentState.currentMaxPrice,
                loadMore = true
            )
        } else {
            Log.d("SearchViewModel", "loadMoreProducts skipped: canLoadMore=${currentState.canLoadMore}, isLoading=${currentState.isLoading}")
        }
    }

    fun loadSearchHistory() {
        val history = searchHistoryManager.getHistory()
        _uiState.value = _uiState.value.copy(
            searchHistory = UiState.Success(history)
        )
    }

    fun setSearchMode(isSearching: Boolean, query: String = "") {
        _uiState.value = _uiState.value.copy(
            isInSearchMode = isSearching,
            lastSearchQuery = query
        )
    }

    fun saveSearchQuery(query: String) {
        searchHistoryManager.saveQuery(query)
        loadSearchHistory()
    }

    fun clearSearchHistory() {
        searchHistoryManager.clearHistory()
        _uiState.value = _uiState.value.copy(
            searchHistory = UiState.Success(emptyList())
        )
    }

    fun resetToHistoryMode() {
        _uiState.value = _uiState.value.copy(
            isInSearchMode = false,
            lastSearchQuery = "",
            products = UiState.Idle,
            currentKeyword = "",
            currentCategories = emptySet(),
            currentMinPrice = null,
            currentMaxPrice = null,
            currentPage = 1,
            totalPages = 1,
            canLoadMore = false,
            isLoading = false
        )
        loadSearchHistory()
    }
}
