package com.blossy.flowerstore.presentation.topProducts.ui

import com.blossy.flowerstore.domain.model.ProductModel

data class PopularUiState (
    val isLoading: Boolean = false,
    val productItems: List<ProductModel> = emptyList(),
    val error: String = ""
)