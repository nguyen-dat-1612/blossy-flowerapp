package com.blossy.flowerstore.presentation.shippingAddress.state

import com.blossy.flowerstore.domain.model.AddressModel
import com.blossy.flowerstore.presentation.common.UiState

data class ShippingAddressUiState (
    val isLoading: Boolean = false,
    val addresses: List<AddressModel> = emptyList(),
    val error: String = "",
    val selectedAddress: AddressModel? = null,
    val addAddress: UiState<AddressModel> = UiState.Idle,
    val updateAddress: UiState<AddressModel> = UiState.Idle,
    val deleteAddress: UiState<AddressModel> = UiState.Idle
)