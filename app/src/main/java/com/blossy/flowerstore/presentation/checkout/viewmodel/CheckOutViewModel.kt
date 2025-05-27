package com.blossy.flowerstore.presentation.checkout.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.model.request.CreateOrderModel
import com.blossy.flowerstore.domain.usecase.cart.GetCartUseCase
import com.blossy.flowerstore.domain.usecase.order.CreateOrderUseCase
import com.blossy.flowerstore.domain.usecase.payment.CreateMomoPaymentUseCase
import com.blossy.flowerstore.domain.usecase.payment.CreateVNPAYPaymentUseCase
import com.blossy.flowerstore.domain.usecase.user.AddressByIdUseCase
import com.blossy.flowerstore.domain.usecase.user.GetDefaultAddressUseCase
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.presentation.checkout.state.CheckOutUiState
import com.blossy.flowerstore.utils.safeUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
@HiltViewModel
class CheckOutViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val getDefaultAddressUseCase: GetDefaultAddressUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val createMomoPaymentUseCase: CreateMomoPaymentUseCase,
    private val createVNPAYPaymentUseCase: CreateVNPAYPaymentUseCase,
    private val addressByIdUseCase: AddressByIdUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(CheckOutUiState())
    val uiState: StateFlow<CheckOutUiState> = _uiState


    private var check = false
    fun setCheck(value: Boolean) {
        check= value
    }
    fun getCheck() = check

    private var payment = false
    fun setPayment(value: Boolean) {
        payment= value
    }
    fun getPayment() = payment

    fun getCheckOut() {
        _uiState.safeUpdate { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val (cartResult, addressResult) = coroutineScope {
                    val cartDeferred = async { withContext(Dispatchers.IO) { getCartUseCase() } }
                    val addressDeferred = async { withContext(Dispatchers.IO) { getDefaultAddressUseCase() } }
                    cartDeferred.await() to addressDeferred.await()
                }

                when (addressResult) {
                    is Result.Success -> {
                        _uiState.safeUpdate { it.copy(selectedAddress = addressResult.data, isLoading = false) }
                    }
                    is Result.Error -> {
                        _uiState.safeUpdate { it.copy(error = addressResult.message, isLoading = false) }
                    }
                    else -> {}
                }

                when (cartResult) {
                    is Result.Success -> {
                        _uiState.safeUpdate { it.copy(cartState = cartResult.data, isLoading = false) }
                    }
                    is Result.Error -> {
                        _uiState.safeUpdate { it.copy(error = cartResult.message, isLoading = false) }
                    }
                    else -> {}
                }

            } catch (e: Exception) {
                _uiState.safeUpdate {
                    it.copy(error = "Error unknown: ${e.message}", isLoading = false)
                }
            }
        }
    }

    fun getAddressById(id: String) {
        _uiState.safeUpdate { it.copy(isLoading = true) }
        viewModelScope.launch {
            when(val result = withContext(Dispatchers.IO) {
                addressByIdUseCase(id)
            }) {
                is Result.Success -> {
                    _uiState.safeUpdate { it.copy(selectedAddress = result.data, isLoading = false) }
                }
                is Result.Error -> {
                    _uiState.safeUpdate { it.copy(error = result.message, isLoading = false) }
                }
                else -> {}
            }

        }
    }

    fun createOrder(orderRequest: CreateOrderModel) {
        Log.d("Alo alo alo:" , "createOrder:")
        viewModelScope.launch {
            when(val result = withContext(Dispatchers.IO) {
                createOrderUseCase(orderRequest)
            }) {
                is Result.Success -> {
                    _uiState.safeUpdate { it.copy(orderResponse = result.data) }
                    if (result.data.data?.paymentMethod == "VNPAY") {
                        createVNPAYPayment(result.data.data.id)
                    }
                }
                is Result.Error -> {
                    _uiState.safeUpdate { it.copy(error = result.message) }
                }
                else -> {}
            }

        }
    }

    fun createMomoPayment(orderId: String) {
        _uiState.safeUpdate { it.copy(isLoading = true) }

        viewModelScope.launch {
            when(val result = withContext(Dispatchers.IO) {
                createMomoPaymentUseCase(orderId)
            }) {
                is Result.Success -> {
                    _uiState.safeUpdate { it.copy(paymentUrl = result.data.paymentUrl) }
                }
                is Result.Error -> {
                    _uiState.safeUpdate { it.copy(error = result.message)}
                }
                else -> {}
            }
        }

    }

    fun createVNPAYPayment(orderId: String) {
        Log.d("Alo alo alo:" , "createVNPAYPayment:")
        _uiState.safeUpdate { it.copy(isLoading = true) }
        viewModelScope.launch {
            when (val result = withContext(Dispatchers.IO) {
                createVNPAYPaymentUseCase(orderId)
            }) {
                is Result.Success -> {
                    _uiState.safeUpdate { it.copy(paymentUrl = result.data.paymentUrl, isLoading = false) }
                }

                is Result.Error -> {
                    _uiState.safeUpdate { it.copy(error = result.message) }
                }

                else -> {}
            }
        }
    }

}