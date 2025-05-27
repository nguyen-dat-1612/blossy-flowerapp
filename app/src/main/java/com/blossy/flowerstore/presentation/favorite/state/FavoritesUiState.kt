package com.blossy.flowerstore.presentation.favorite.state

import com.blossy.flowerstore.domain.model.ProductModel
import com.blossy.flowerstore.presentation.common.UiState

data class FavoritesUiState (
    val isLoading : Boolean = false,
    val productItems: List<ProductModel> = emptyList(),
    val errorMessage: String? = null,
    val toggleFavoriteState: UiState<Boolean> = UiState.Idle
)