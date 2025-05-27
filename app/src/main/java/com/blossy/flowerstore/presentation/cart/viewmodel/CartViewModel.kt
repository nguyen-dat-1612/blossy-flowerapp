package com.blossy.flowerstore.presentation.cart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.model.request.UpdateCartModel
import com.blossy.flowerstore.domain.usecase.cart.GetCartUseCase
import com.blossy.flowerstore.domain.usecase.cart.RemoveCartUseCase
import com.blossy.flowerstore.domain.usecase.cart.UpdateCartUseCase
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.presentation.cart.state.CartUiState
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val updateCartUseCase: UpdateCartUseCase,
    private val removeCartUseCase: RemoveCartUseCase
) : ViewModel() {

    private val _cartUiState = MutableStateFlow(CartUiState())
    val cartUiState: StateFlow<CartUiState> = _cartUiState

    fun getCart() {
        _cartUiState.update { it.copy(isLoadingCart = true, errorMessage = null) }

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                getCartUseCase()
            }
            when (result) {
                is Result.Success -> {
                    _cartUiState.update {
                        it.copy(
                            isLoadingCart = false,
                            cartItems = result.data,
                            errorMessage = null
                        )
                    }
                }

                is Result.Error -> {
                    _cartUiState.update {
                        it.copy(
                            isLoadingCart = false,
                            errorMessage = result.message
                        )
                    }
                }

                else -> {
                    _cartUiState.update {
                        it.copy(
                            isLoadingCart = false,
                            cartItems = emptyList(),
                            errorMessage = "Unknown error occurred"
                        )
                    }
                }
            }
        }
    }

    fun updateCartItemQuantity(
        productId: String,
        quantity: Int
    ) {
        _cartUiState.update { it.copy(updateCartState = UiState.Loading) }
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                updateCartUseCase(UpdateCartModel(productId, quantity))
            }
            when(result) {
                is Result.Success -> {
                    val updatedItems = _cartUiState.value.cartItems.map { cartItem ->
                        if (cartItem.product.id == productId) {
                            cartItem.copy(quantity = quantity)
                        } else {
                            cartItem
                        }
                    }

                    _cartUiState.update {
                        it.copy(
                            updateCartState = UiState.Success(result.data),
                            cartItems = updatedItems
                        )
                    }
                }

                is Result.Error -> {
                    _cartUiState.update {
                        it.copy(updateCartState = UiState.Error(result.message))
                    }
                }

                else -> Unit
            }

            delay(500)
            _cartUiState.update { it.copy(updateCartState = UiState.Idle) }
        }
    }


    fun deleteCartItem(productId: String) {
        _cartUiState.update { it.copy(removeCartState = UiState.Loading) }
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                removeCartUseCase(productId)
            }

            when(result) {
                is Result.Success -> {
                    val filteredItems = _cartUiState.value.cartItems.filter {
                        it.product.id != productId
                    }

                    _cartUiState.update {
                        it.copy(
                            removeCartState = UiState.Success(true),
                            cartItems = filteredItems
                        )
                    }
                }

                is Result.Error -> {
                    _cartUiState.update {
                        it.copy(removeCartState = UiState.Error(result.message))
                    }
                }

                else -> Unit
            }

            delay(500)
            _cartUiState.update { it.copy(removeCartState = UiState.Idle) }
        }
    }

    companion object {
        private const val TAG = "CartViewModel"
    }
}