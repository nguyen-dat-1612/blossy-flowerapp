package com.blossy.flowerstore.presentation.shippingAddress.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.model.AddressModel
import com.blossy.flowerstore.domain.usecase.user.AddAddressUseCase
import com.blossy.flowerstore.domain.usecase.user.DeleteAddressUseCase
import com.blossy.flowerstore.domain.usecase.user.GetUserAddressesUseCase
import com.blossy.flowerstore.domain.usecase.user.UpdateAddressUseCase
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.shippingAddress.state.ShippingAddressUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ShippingAddressViewModel @Inject constructor(
    private val getUserAddressesUseCase: GetUserAddressesUseCase,
    private val addAddressUseCase: AddAddressUseCase,
    private val updateAddressUseCase: UpdateAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase
) : ViewModel() {

    private val _shippingAddressUiState = MutableStateFlow(ShippingAddressUiState())
    val shippingAddressUiState = _shippingAddressUiState

    fun getUserAddresses() {
        _shippingAddressUiState.update { it.copy(isLoading = true, error = "") }

        viewModelScope.launch {
            when (val result = withContext(Dispatchers.IO) { getUserAddressesUseCase() }) {
                is Result.Success -> {
                    _shippingAddressUiState.update { it.copy(
                        isLoading = false,
                        addresses = result.data,
                        error = ""
                    ) }
                }
                is Result.Error -> {
                    _shippingAddressUiState.update { it.copy(
                        isLoading = false,
                        error = result.message
                    ) }
                }
                is Result.Empty -> {
                    _shippingAddressUiState.update { it.copy(
                        isLoading = false,
                        addresses = emptyList(),
                        error = "No addresses found"
                    ) }
                }
            }
        }
    }

    fun addAddress(address: AddressModel) {
        _shippingAddressUiState.update { it.copy(
            isLoading = true,
            error = "",
            addAddress = UiState.Loading
        ) }

        viewModelScope.launch {
            when (val response = withContext(Dispatchers.IO) { addAddressUseCase(address) }) {
                is Result.Success -> {
                    _shippingAddressUiState.update { currentState ->
                        currentState.copy(
                            addresses = currentState.addresses + response.data,
                            addAddress = UiState.Success(response.data)
                        )
                    }
                }
                is Result.Error -> {
                    _shippingAddressUiState.update { it.copy(
                        addAddress = UiState.Error(response.message)
                    ) }
                }
                else -> Unit
            }
            delay(1000)
            _shippingAddressUiState.update {
                it.copy(
                    addAddress = UiState.Idle
                )
            }
        }
    }

    fun updateAddress(address: AddressModel) {
        _shippingAddressUiState.update { it.copy(
            updateAddress = UiState.Loading
        ) }

        viewModelScope.launch {
            when (val response = withContext(Dispatchers.IO) { updateAddressUseCase(address) }) {
                is Result.Success -> {
                    val updatedList = _shippingAddressUiState.value.addresses.map {
                        if (it.id == response.data.id) response.data else it
                    }

                    _shippingAddressUiState.update {
                        it.copy(
                            addresses = updatedList,
                            updateAddress = UiState.Success(response.data)
                        )
                    }
                }
                is Result.Error -> {
                    _shippingAddressUiState.update { it.copy(
                        updateAddress = UiState.Error(message = response.message)
                    ) }
                }
                else -> Unit
            }
            delay(1000)
            _shippingAddressUiState.update {
                it.copy(
                    updateAddress = UiState.Idle
                )
            }
        }
    }

    fun deleteAddress(addressId: String) {
        _shippingAddressUiState.update { it.copy(
            deleteAddress = UiState.Loading
        ) }

        viewModelScope.launch {
            when (val response = withContext(Dispatchers.IO) { deleteAddressUseCase(addressId) }) {
                is Result.Success -> {
                    val updatedList = _shippingAddressUiState.value.addresses.filterNot { it.id == addressId }
                    val deletedAddress = _shippingAddressUiState.value.addresses.find { it.id == addressId }

                    _shippingAddressUiState.update {
                        it.copy(
                            addresses = updatedList,
                            deleteAddress = deletedAddress?.let { address -> UiState.Success(address) } ?: UiState.Idle
                        )
                    }
                }
                is Result.Error -> {
                    _shippingAddressUiState.update { it.copy(
                        deleteAddress = UiState.Error(response.message)
                    ) }
                }
                else -> Unit
            }
        }
    }

    fun selectAddress(address: AddressModel) {
        _shippingAddressUiState.update { it.copy(selectedAddress = address) }
    }

    fun resetAddAddressState() {
        _shippingAddressUiState.update { it.copy(addAddress = UiState.Idle) }
    }

    fun resetDeleteAddressState() {
        _shippingAddressUiState.update { it.copy(deleteAddress = UiState.Idle) }
    }
}