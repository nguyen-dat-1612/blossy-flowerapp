package com.blossy.flowerstore.presentation.category.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.data.remote.dto.ProductResponse
import com.blossy.flowerstore.domain.model.Category
import com.blossy.flowerstore.domain.usecase.category.GetCategoriesUseCase
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
class CategoryViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
) : ViewModel()  {

    private val _productsUiState = MutableStateFlow<UiState<ProductResponse>>(UiState.Loading)
    val productsUiState: StateFlow<UiState<ProductResponse>> = _productsUiState

    private val _categoryUiState = MutableStateFlow<UiState<List<Category>>>(UiState.Idle)
    val categoryUiState: StateFlow<UiState<List<Category>>> = _categoryUiState

    fun loadProducts(
        keyword: String ?= null,
        categories: Set<String> = emptySet(),
        minPrice: Int? = null,
        maxPrice: Int? = null,
        page: Int = 1
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _productsUiState.value = UiState.Loading
            when (val result = getProductsUseCase.invoke(categories = categories)) {
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

    fun loadCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            _categoryUiState.value = UiState.Loading

            when (val result = getCategoriesUseCase.invoke()) {
                is Result.Success -> {
                    _categoryUiState.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _categoryUiState.value = UiState.Error(result.message)
                }
                is Result.Empty -> {
                }
            }
        }
    }


}