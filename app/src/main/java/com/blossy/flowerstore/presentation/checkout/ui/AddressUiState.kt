package com.blossy.flowerstore.presentation.checkout.ui

import com.blossy.flowerstore.domain.model.Address

sealed interface AddressUiState {
    object Idle : AddressUiState
    object Loading : AddressUiState
    data class Success(val data: Address) : AddressUiState
    object NoAddress : AddressUiState
    data class Error(val message: String) : AddressUiState
}
