package com.blossy.flowerstore.presentation.cart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.model.CartItem
import com.blossy.flowerstore.domain.usecase.cart.GetCartUseCase
import com.blossy.flowerstore.domain.usecase.cart.RemoveCartUseCase
import com.blossy.flowerstore.domain.usecase.cart.UpdateCartUseCase
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val updateCartUseCase: UpdateCartUseCase,
    private val removeCartUseCase: RemoveCartUseCase
) : ViewModel() {

    private val _getCartUiState = MutableStateFlow<UiState<List<CartItem>>>(UiState.Idle)
    val getCartUiState: StateFlow<UiState<List<CartItem>>> = _getCartUiState

    private val _updateCartUiState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val updateCartUiState: StateFlow<UiState<Boolean>> = _updateCartUiState

    private val _removeCartUiState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val removeCartUiState: StateFlow<UiState<Boolean>> = _removeCartUiState

    var lastUpdatedProductId: String = ""
    var lastUpdatedQuantity: Int = 0
    var lastRemovedProductId: String = ""

    fun getCart() {
        viewModelScope.launch(Dispatchers.IO) {
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

    fun updateCartItemQuantity(
        productId: String,
        quantity: Int
    ) {
        lastUpdatedProductId = productId
        lastUpdatedQuantity = quantity

        viewModelScope.launch(Dispatchers.IO) {
            _updateCartUiState.value = UiState.Loading
            when(val result = updateCartUseCase.invoke(productId, quantity)) {
                is Result.Success -> {
                    _updateCartUiState.value = UiState.Success(true)
                    getCart()
                }
                is Result.Error -> {
                    _updateCartUiState.value = UiState.Error(result.message)
                }
                else -> {}
            }
            delay(500)
            _updateCartUiState.value = UiState.Idle
        }
    }

    fun deleteCartItem(productId: String) {
        lastRemovedProductId = productId

        viewModelScope.launch(Dispatchers.IO) {
            _removeCartUiState.value = UiState.Loading
            when(val result = removeCartUseCase.invoke(productId)) {
                is Result.Success -> {
                    _removeCartUiState.value = UiState.Success(true)
                    getCart()
                }
                is Result.Error -> {
                    _removeCartUiState.value = UiState.Error(result.message)
                }
                else -> {}
            }
            delay(500)
            _removeCartUiState.value = UiState.Idle

        }
    }
}