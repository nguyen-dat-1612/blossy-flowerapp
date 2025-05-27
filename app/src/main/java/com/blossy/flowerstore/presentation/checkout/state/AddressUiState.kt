package com.blossy.flowerstore.presentation.checkout.state

import com.blossy.flowerstore.domain.model.AddressModel

sealed interface AddressUiState {
    object Idle : AddressUiState
    object Loading : AddressUiState
    data class Success(val data: AddressModel) : AddressUiState
    object NoAddress : AddressUiState
    data class Error(val message: String) : AddressUiState
}
