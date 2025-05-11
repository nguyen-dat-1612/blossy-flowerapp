package com.blossy.flowerstore.presentation.topProducts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.model.Product
import com.blossy.flowerstore.domain.usecase.product.GetTopProductsUseCase
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result

@HiltViewModel
class PopularViewModel @Inject constructor(
    private val getTopProductsUseCase: GetTopProductsUseCase
) : ViewModel() {
    private val _productUiState = MutableStateFlow<UiState<List<Product>>>(UiState.Idle)
    val productUiState: StateFlow<UiState<List<Product>>> = _productUiState

    fun fetchPopularProducts() {
        viewModelScope.launch (Dispatchers.IO) {
            _productUiState.value = UiState.Loading
            when (val result = getTopProductsUseCase()) {
                is Result.Success -> {
                    _productUiState.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _productUiState.value = UiState.Error(result.message)
                    }
                else ->  {

                }
            }
        }
    }
}