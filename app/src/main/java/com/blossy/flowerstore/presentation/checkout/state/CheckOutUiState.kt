package com.blossy.flowerstore.presentation.checkout.state

import com.blossy.flowerstore.data.remote.utils.OrderResponseWrapper
import com.blossy.flowerstore.domain.model.AddressModel
import com.blossy.flowerstore.domain.model.CartItemModel

data class CheckOutUiState(
    val isLoading: Boolean = true,
    val cartState: List<CartItemModel> ?= null,
    val selectedAddress: AddressModel ?= null,
    val orderResponse: OrderResponseWrapper ?= null,
    val paymentUrl: String ?= null,
    val error: String = ""
)