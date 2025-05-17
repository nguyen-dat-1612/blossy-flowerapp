package com.blossy.flowerstore.presentation.shippingAddress.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.data.remote.dto.AddressResponse
import com.blossy.flowerstore.domain.model.Address
import com.blossy.flowerstore.domain.usecase.user.AddAddressUseCase
import com.blossy.flowerstore.domain.usecase.user.DeleteAddressUseCase
import com.blossy.flowerstore.domain.usecase.user.UpdateAddressUseCase
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val addAddressUseCase: AddAddressUseCase,
    private val updateAddressUseCase: UpdateAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Address>>(UiState.Idle)
    var uiState: MutableStateFlow<UiState<Address>> = _uiState


    fun addAddress(address: AddressResponse) {
        viewModelScope.launch (Dispatchers.IO) {
            _uiState.value = UiState.Loading
            when(val response = addAddressUseCase.invoke(address)) {
                is Result.Success -> {
                    _uiState.value = UiState.Success(response.data)
                }
                is Result.Error -> {
                    _uiState.value = UiState.Error(response.message)
                }
                else -> {

                }
            }
        }
    }

    fun updateAddress(address: AddressResponse) {
        viewModelScope.launch (Dispatchers.IO) {
            _uiState.value = UiState.Loading
            when(val response = updateAddressUseCase.invoke(address)) {
                is Result.Success -> {
                    _uiState.value = UiState.Success(response.data)
                }
                is Result.Error -> {
                    _uiState.value = UiState.Error(response.message)
                }
                else -> {

                }
            }
        }
    }

    fun deleteAddress(addressId: String) {
        viewModelScope.launch (Dispatchers.IO) {
            _uiState.value = UiState.Loading

            when(val response = deleteAddressUseCase.invoke(addressId)) {
                is Result.Success -> {
                    _uiState.value = UiState.Success(response.data)
                }

                is Result.Error -> {
                    _uiState.value = UiState.Error(response.message)
                }

                else -> {

                }
            }
        }
    }
}