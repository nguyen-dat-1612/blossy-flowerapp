package com.blossy.flowerstore.presentation.cart.state

import com.blossy.flowerstore.domain.model.CartItemModel
import com.blossy.flowerstore.presentation.common.UiState

data class CartUiState(
    val cartItems: List<CartItemModel> = emptyList(),
    val isLoadingCart: Boolean = false,
    val updateCartState: UiState<Boolean> = UiState.Idle,
    val removeCartState: UiState<Boolean> = UiState.Idle,
    val errorMessage: String? = null
)