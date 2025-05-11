package com.blossy.flowerstore.presentation.order.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.model.Order
import com.blossy.flowerstore.domain.usecase.order.GetOrderByIdUseCase
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result


@HiltViewModel
class DetailOrderViewModel @Inject constructor(
    private val getOrderByIdUseCase: GetOrderByIdUseCase
) : ViewModel(){
    private val _order = MutableStateFlow<UiState<Order>>(UiState.Idle)
    val order: StateFlow<UiState<Order>> = _order

    fun getOrderById(id: String) {
        viewModelScope.launch (Dispatchers.IO){
            _order.value = UiState.Loading

            when(val response = getOrderByIdUseCase.invoke(id)) {
                is Result.Success -> {
                    _order.value = UiState.Success(response.data)
                }
                is Result.Error -> {
                    _order.value = UiState.Error(response.message)
                }
                else -> {}
            }

        }
    }
}