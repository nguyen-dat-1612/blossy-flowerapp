package com.blossy.flowerstore.presentation.category.state

import com.blossy.flowerstore.domain.model.CategoryModel
import com.blossy.flowerstore.domain.model.response.ProductListModel
import com.blossy.flowerstore.presentation.common.UiState

data class CategoryUiState (
    val isLoading: Boolean = false,
    val categories: List<CategoryModel> = emptyList(),
    val errorMessage: String? = null,
    val getProductsState: UiState<ProductListModel> = UiState.Idle
)