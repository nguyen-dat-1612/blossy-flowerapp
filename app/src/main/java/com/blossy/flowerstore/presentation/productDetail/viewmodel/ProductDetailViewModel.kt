package com.blossy.flowerstore.presentation.productDetail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.model.Product
import com.blossy.flowerstore.domain.usecase.cart.AddToCartUseCase
import com.blossy.flowerstore.domain.usecase.favorite.AddFavoriteUseCase
import com.blossy.flowerstore.domain.usecase.favorite.CheckFavoriteUseCase
import com.blossy.flowerstore.domain.usecase.favorite.RemoveFavoriteUseCase
import com.blossy.flowerstore.domain.usecase.product.GetProductByIdUseCase
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val checkFavoriteUseCase: CheckFavoriteUseCase

): ViewModel() {
    private val _productDetailUiState = MutableStateFlow<UiState<Product>>(UiState.Idle)
    var productDetailUiState: StateFlow<UiState<Product>> = _productDetailUiState

    private val _addToCartUiState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    var addToCartUiState: StateFlow<UiState<Boolean>> = _addToCartUiState

    private val _addFavoriteUiState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    var addFavoriteUiState: StateFlow<UiState<Boolean>> = _addFavoriteUiState

    private val _removeFavoriteUiState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    var removeFavoriteUiState: StateFlow<UiState<Boolean>> = _removeFavoriteUiState

    private val _checkFavoriteUiState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    var checkFavoriteUiState: StateFlow<UiState<Boolean>> = _checkFavoriteUiState

    fun getProductDetail(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _productDetailUiState.value = UiState.Loading
            _checkFavoriteUiState.value = UiState.Loading

            try {
                supervisorScope {
                    val productDeferred = async { getProductByIdUseCase(id) }
                    val favoriteDeferred = async { checkFavoriteUseCase(id) }

                    val productResult = runCatching { productDeferred.await() }.getOrElse {
                        _productDetailUiState.value = UiState.Error("Lỗi khi lấy chi tiết sản phẩm: ${it.message}")
                        null
                    }

                    val favoriteResult = runCatching { favoriteDeferred.await() }.getOrElse {
                        _checkFavoriteUiState.value = UiState.Error("Lỗi khi kiểm tra yêu thích: ${it.message}")
                        null
                    }
                    productResult?.let {
                        when (it) {
                            is Result.Success -> _productDetailUiState.value = UiState.Success(it.data)
                            is Result.Error -> _productDetailUiState.value = UiState.Error(it.message)
                            is Result.Empty -> _productDetailUiState.value = UiState.Error("Không tìm thấy sản phẩm")
                        }
                    }

                    favoriteResult?.let {
                        when (it) {
                            is Result.Success -> _checkFavoriteUiState.value = UiState.Success(it.data)
                            is Result.Error -> _checkFavoriteUiState.value = UiState.Error(it.message)
                            is Result.Empty -> _checkFavoriteUiState.value = UiState.Success(false)
                        }
                    }
                }
            } catch (e: Exception) {
                _productDetailUiState.value = UiState.Error("Lỗi không xác định: ${e.message}")
                _checkFavoriteUiState.value = UiState.Error("Lỗi không xác định: ${e.message}")
            }
        }
    }


    fun addToCart(
        productId: String,
        quantity: Int
    ) {
        viewModelScope.launch (Dispatchers.IO) {
            _addToCartUiState.value = UiState.Loading
            when (val response = addToCartUseCase.invoke(productId, quantity)) {
                is Result.Success -> {
                    _addToCartUiState.value = UiState.Success(true)
                }
                is Result.Error -> {
                    _addToCartUiState.value = UiState.Error(response.message)
                }
                is Result.Empty -> {

                }
            }
        }
    }

    fun addFavorite(
        productId: String
    ) {
        viewModelScope.launch (Dispatchers.IO) {
            _addFavoriteUiState.value = UiState.Loading

            when (val response = addFavoriteUseCase.invoke(productId)) {
                is Result.Success -> {
                    _addFavoriteUiState.value = UiState.Success(true)
                    _checkFavoriteUiState.value = UiState.Success(true)
                }
                is Result.Error -> {
                    _addFavoriteUiState.value = UiState.Error(response.message)
                }
                else -> {}
            }
        }
    }

    fun removeFavorite(
        productId: String
    ) {
        viewModelScope.launch (Dispatchers.IO) {
            _removeFavoriteUiState.value = UiState.Loading
            when (val response = removeFavoriteUseCase.invoke(productId)) {
                is Result.Success -> {
                    _removeFavoriteUiState.value = UiState.Success(true)
                    _checkFavoriteUiState.value = UiState.Success(false)
                }

                is Result.Error -> {
                    _removeFavoriteUiState.value = UiState.Error(response.message)
                }

                else -> {}
            }
        }
    }

}