package com.blossy.flowerstore.presentation.productDetail.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.model.request.AddToCartModel
import com.blossy.flowerstore.domain.usecase.cart.AddToCartUseCase
import com.blossy.flowerstore.domain.usecase.favorite.AddFavoriteUseCase
import com.blossy.flowerstore.domain.usecase.favorite.CheckFavoriteUseCase
import com.blossy.flowerstore.domain.usecase.favorite.RemoveFavoriteUseCase
import com.blossy.flowerstore.domain.usecase.product.GetProductByIdUseCase
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.productDetail.state.ProductDetailUiState
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
class ProductDetailViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val checkFavoriteUseCase: CheckFavoriteUseCase

): ViewModel() {

    private val _productDetailUiState = MutableStateFlow(ProductDetailUiState())
    var productDetailUiState: StateFlow<ProductDetailUiState> = _productDetailUiState

    fun getProductDetail(id: String) {
        _productDetailUiState.value = _productDetailUiState.value.copy(isLoading = true, isFavorite = false)

        viewModelScope.launch {
            try {
                val (productResult, favoriteResult) = coroutineScope {
                    val productDeferred = async { withContext(Dispatchers.IO) { getProductByIdUseCase(id) } }
                    val favoriteDeferred = async { withContext(Dispatchers.IO) { checkFavoriteUseCase(id) } }
                    productDeferred.await() to favoriteDeferred.await()
                }

                when (productResult) {
                    is Result.Success -> _productDetailUiState.value =
                        _productDetailUiState.value.copy(
                            isLoading = false,
                            product = productResult.data
                        )

                    is Result.Error -> _productDetailUiState.value =
                        _productDetailUiState.value.copy(
                            isLoading = false,
                            error = productResult.message
                        )

                    is Result.Empty -> _productDetailUiState.value =
                        _productDetailUiState.value.copy(
                            isLoading = false,
                            error = "Không tìm thấy sản phẩm"
                        )
                }

                when (favoriteResult) {
                    is Result.Success -> _productDetailUiState.value = _productDetailUiState.value.copy(isFavorite = favoriteResult.data)
                    is Result.Error -> _productDetailUiState.value = _productDetailUiState.value.copy(error = favoriteResult.message)
                    else -> Unit
                }

            } catch (e: Exception) {
                _productDetailUiState.value = ProductDetailUiState(error = "Lỗi không xác định: ${e.message}")
            }
        }
    }

    fun addToCart(
        productId: String,
        quantity: Int
    ) {
        _productDetailUiState.value = _productDetailUiState.value.copy(addToCartSuccess = UiState.Loading)
        viewModelScope.launch {
            when (val response = withContext(Dispatchers.IO){ addToCartUseCase(AddToCartModel(productId, quantity))}){
                is Result.Success -> {
                    _productDetailUiState.value = _productDetailUiState.value.copy(addToCartSuccess = UiState.Success(true))
                }
                is Result.Error -> {
                    _productDetailUiState.value = _productDetailUiState.value.copy(addToCartSuccess = UiState.Error(response.message))
                }
                else -> Unit
            }
        }
    }

    fun addFavorite(
        productId: String
    ) {
        viewModelScope.launch {
            when (val response = withContext(Dispatchers.IO) {
                addFavoriteUseCase(productId)
            } ) {
                is Result.Success -> {
                    _productDetailUiState.value = _productDetailUiState.value.copy(isFavorite = true)
                }
                is Result.Error -> {
                    _productDetailUiState.value = _productDetailUiState.value.copy(error = response.message)
                    Log.d(TAG, "addFavorite: ${response.message}")
                }
                else -> {}
            }
        }
    }

    fun removeFavorite(
        productId: String
    ) {
        viewModelScope.launch {
            when (val response = withContext(Dispatchers.IO) {
                removeFavoriteUseCase(productId)
            }) {
                is Result.Success -> {
                    _productDetailUiState.value = _productDetailUiState.value.copy(isFavorite = false)
                }

                is Result.Error -> {
                    _productDetailUiState.value = _productDetailUiState.value.copy(error = response.message)
                }

                else -> {}
            }
        }
    }
    companion object {
        private const val TAG = "ProductDetailViewModel"

    }

}