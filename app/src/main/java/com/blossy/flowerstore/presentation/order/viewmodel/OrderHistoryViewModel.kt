package com.blossy.flowerstore.presentation.order.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.model.OrderModel
import com.blossy.flowerstore.domain.usecase.order.CancelOrderUseCase
import com.blossy.flowerstore.domain.usecase.order.ConfirmOrderUseCase
import com.blossy.flowerstore.domain.usecase.order.MyOrdersUseCase
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result
import kotlinx.coroutines.withContext


@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val myOrdersUseCase: MyOrdersUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase,
    private val confirmOrderUseCase: ConfirmOrderUseCase
) : ViewModel(){

    private val _orderHistory = MutableStateFlow<UiState<List<OrderModel>>>(UiState.Idle)
    val orderHistory: StateFlow<UiState<List<OrderModel>>> = _orderHistory

    private val _cancelOrder = MutableStateFlow<UiState<OrderModel>>(UiState.Idle)
    val cancelOrder: StateFlow<UiState<OrderModel>> = _cancelOrder

    private val _confirmOrder = MutableStateFlow<UiState<OrderModel>>(UiState.Idle)
    val confirmOrder: StateFlow<UiState<OrderModel>> = _confirmOrder

    fun fetchOrderHistory(
        status: String,
        page: Int,
        limit: Int,
        isPaid: Boolean,
        sort: String
    ) {
        _orderHistory.value = UiState.Loading
        viewModelScope.launch {
            when(val response = withContext(Dispatchers.IO) {
                myOrdersUseCase.invoke(

                    status, page, limit, isPaid, sort
                )
            }){
                is Result.Success -> {
                    _orderHistory.value = UiState.Success(response.data)
                }
                is Result.Error -> {
                    _orderHistory.value = UiState.Error(response.message)
                }
                else -> {}
            }

        }
    }

    fun cancelOrder(id: String, reason: String) {
        viewModelScope.launch (Dispatchers.IO) {
            _cancelOrder.value = UiState.Loading
            when(val response = cancelOrderUseCase.invoke(id, reason)) {
                is Result.Success -> {
                    _cancelOrder.value = UiState.Success(response.data)
                }
                is Result.Error -> {
                    _cancelOrder.value = UiState.Error(response.message)
                }
                else -> {}
            }
        }

    }

    fun removeOrderFromList(orderId: String) {
        val current = (orderHistory.value as? UiState.Success)?.data ?: return
        val updated = current.filter { it.id != orderId }
        _orderHistory.value = UiState.Success(updated)
    }

    fun confirmOrder(id: String) {
        viewModelScope.launch (Dispatchers.IO) {
            _confirmOrder.value = UiState.Loading

            when(val response = confirmOrderUseCase.invoke(id)) {
                is Result.Success -> {
                    _confirmOrder.value = UiState.Success(response.data)
                }
                is Result.Error -> {
                    _confirmOrder.value = UiState.Error(response.message)
                }
                else -> {}
            }
        }
    }

    fun confirmOrderFromList(orderId: String) {
        val current = (orderHistory.value as? UiState.Success)?.data ?: return
        val updated = current.map {
            if (it.id == orderId) {
                it.copy(isDelivered = true)
            } else {
                it
            }
        }
        _orderHistory.value = UiState.Success(updated)

    }
}