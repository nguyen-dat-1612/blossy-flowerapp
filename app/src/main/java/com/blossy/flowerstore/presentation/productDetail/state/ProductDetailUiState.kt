package com.blossy.flowerstore.presentation.productDetail.state

import com.blossy.flowerstore.domain.model.ProductModel
import com.blossy.flowerstore.presentation.common.UiState

data class ProductDetailUiState (
    val isLoading: Boolean = false,
    val product: ProductModel? = null,
    val isFavorite: Boolean = false,
    val addToCartSuccess: UiState<Boolean> = UiState.Idle,
    val error: String = ""
)