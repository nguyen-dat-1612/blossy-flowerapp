package com.blossy.flowerstore.presentation.category.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.data.remote.dto.ProductResponse
import com.blossy.flowerstore.domain.usecase.product.GetProductsUseCase
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryProductViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel()  {

    private val _productsUiState = MutableStateFlow<UiState<ProductResponse>>(UiState.Loading)
    val productsUiState: StateFlow<UiState<ProductResponse>> = _productsUiState


    fun loadProducts(
        category: String? = null,
        keyword: String ? = null,
        page: Int? = 1,
        limit: Int? = 10
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _productsUiState.value = UiState.Loading
            when (val result = getProductsUseCase.invoke(keyword!!)) {
                is Result.Success -> {
                    _productsUiState.value = UiState.Success(result.data)
                }

                is Result.Error -> {
                    _productsUiState.value = UiState.Error(result.message)
                }
                is Result.Empty -> {
                }
            }
        }
    }
}