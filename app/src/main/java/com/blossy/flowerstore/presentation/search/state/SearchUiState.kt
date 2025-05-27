package com.blossy.flowerstore.presentation.search.state

import com.blossy.flowerstore.domain.model.response.ProductListModel
import com.blossy.flowerstore.presentation.common.UiState

data class SearchUiState(
    val isInSearchMode: Boolean = false,
    val lastSearchQuery: String = "",
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val currentKeyword: String = "",
    val currentCategories: Set<String> = emptySet(),
    val currentMinPrice: Int? = null,
    val currentMaxPrice: Int? = null,
    val isLoading: Boolean = false,
    val canLoadMore: Boolean = false,
    val products: UiState<ProductListModel> = UiState.Idle,
    val searchHistory: UiState<List<String>> = UiState.Idle
)