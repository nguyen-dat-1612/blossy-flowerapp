package com.blossy.flowerstore.presentation.checkout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.data.remote.dto.CreateOrderRequest
import com.blossy.flowerstore.data.remote.utils.OrderResponseWrapper
import com.blossy.flowerstore.domain.model.Address
import com.blossy.flowerstore.domain.model.CartItem
import com.blossy.flowerstore.domain.usecase.cart.GetCartUseCase
import com.blossy.flowerstore.domain.usecase.order.CreateOrderUseCase
import com.blossy.flowerstore.domain.usecase.payment.CreateMomoPaymentUseCase
import com.blossy.flowerstore.domain.usecase.payment.CreateVNPAYPaymentUseCase
import com.blossy.flowerstore.domain.usecase.user.GetDefaultAddressUseCase
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class CheckOutViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val getDefaultAddressUseCase: GetDefaultAddressUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val createMomoPaymentUseCase: CreateMomoPaymentUseCase,
    private val createVNPAYPaymentUseCase: CreateVNPAYPaymentUseCase
): ViewModel() {

    private var isAddressManuallySelected = false


    private val _getCartUiState = MutableStateFlow<UiState<List<CartItem>>>(UiState.Idle)
    var getCartUiState: StateFlow<UiState<List<CartItem>>> = _getCartUiState

    private val _getDefaultAddressUiState = MutableStateFlow<UiState<Address>>(UiState.Idle)
    var getDefaultAddressUiState: StateFlow<UiState<Address>> = _getDefaultAddressUiState

    private val _createOrderUiState = MutableStateFlow<UiState<OrderResponseWrapper>>(UiState.Idle)
    var createOrderUiState: StateFlow<UiState<OrderResponseWrapper>> = _createOrderUiState

    private val _createMomoPaymentUiState = MutableStateFlow<UiState<String>>(UiState.Idle)
    var createMomoPaymentUiState: StateFlow<UiState<String>> = _createMomoPaymentUiState

    private val _createVNPAYPaymentUiState = MutableStateFlow<UiState<String>>(UiState.Idle)
    var createVNPAYPaymentUiState: StateFlow<UiState<String>> = _createVNPAYPaymentUiState

    fun getCart() {
        viewModelScope.launch (Dispatchers.IO) {
            _getCartUiState.value = UiState.Loading
            when(val result = getCartUseCase.invoke()) {
                is Result.Success -> {
                    _getCartUiState.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _getCartUiState.value = UiState.Error(result.message)
                }
                else -> {}
            }
        }
    }

    fun getDefaultAddress() {
        viewModelScope.launch (Dispatchers.IO) {
            _getDefaultAddressUiState.value = UiState.Loading
            when(val result = getDefaultAddressUseCase.invoke()) {
                is Result.Success -> {
                    _getDefaultAddressUiState.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _getDefaultAddressUiState.value = UiState.Error(result.message)
                }
                else -> {}
            }
        }
    }

    fun createOrder(orderRequest: CreateOrderRequest) {
        viewModelScope.launch (Dispatchers.IO) {
            _createOrderUiState.value = UiState.Loading

            when(val result = createOrderUseCase.invoke(orderRequest)) {
                is Result.Success -> {
                    _createOrderUiState.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _createOrderUiState.value = UiState.Error(result.message)
                    }
                else -> {}
            }

        }
    }

    fun createMomoPayment(orderId: String) {
        viewModelScope.launch (Dispatchers.IO) {
            _createMomoPaymentUiState.value = UiState.Loading
            when(val result = createMomoPaymentUseCase.invoke(orderId)) {
                is Result.Success -> {
                    _createMomoPaymentUiState.value = UiState.Success(result.data.paymentUrl)
                }
                is Result.Error -> {
                    _createMomoPaymentUiState.value = UiState.Error(result.message)
                }
                else -> {}
            }
        }

    }

    fun createVNPAYPayment(orderId: String) {
        viewModelScope.launch (Dispatchers.IO) {
            _createVNPAYPaymentUiState.value = UiState.Loading
            when (val result = createVNPAYPaymentUseCase.invoke(orderId)) {
                is Result.Success -> {
                    _createVNPAYPaymentUiState.value = UiState.Success(result.data.paymentUrl)
                }

                is Result.Error -> {
                    _createVNPAYPaymentUiState.value = UiState.Error(result.message)
                }

                else -> {}
            }
        }
    }

    fun isAddressManuallySelected(): Boolean = isAddressManuallySelected
    fun setAddressManuallySelected(value: Boolean) {
        isAddressManuallySelected = value
    }


}