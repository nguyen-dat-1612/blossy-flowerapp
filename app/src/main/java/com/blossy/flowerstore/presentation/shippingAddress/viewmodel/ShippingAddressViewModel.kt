package com.blossy.flowerstore.presentation.shippingAddress.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.model.Address
import com.blossy.flowerstore.domain.usecase.user.GetUserAddressesUseCase
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result

@HiltViewModel
class ShippingAddressViewModel @Inject constructor(
    private val getUserAddressesUseCase: GetUserAddressesUseCase

): ViewModel() {

    private val _addresses = MutableStateFlow<UiState<List<Address>>>(UiState.Idle)
    val addresses: StateFlow<UiState<List<Address>>> = _addresses

    fun getUserAddresses() {
        viewModelScope.launch (Dispatchers.IO) {
            _addresses.value = UiState.Loading
            when(val result = getUserAddressesUseCase.invoke()) {
                is Result.Success -> {
                    _addresses.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _addresses.value = UiState.Error(result.message)
                }
                else -> {}
            }
        }
    }

}