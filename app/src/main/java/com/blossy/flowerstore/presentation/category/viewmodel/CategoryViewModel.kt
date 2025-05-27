package com.blossy.flowerstore.presentation.category.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.usecase.category.GetCategoriesUseCase
import com.blossy.flowerstore.domain.usecase.product.GetProductsUseCase
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.presentation.category.state.CategoryUiState
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
) : ViewModel()  {

    private val _categoryUiState = MutableStateFlow(CategoryUiState())
    val categoryUiState: StateFlow<CategoryUiState> = _categoryUiState

    init {
        loadCategories()
    }

    fun loadCategories() {
        _categoryUiState.value = _categoryUiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch(Dispatchers.IO) {
            val result = withContext(Dispatchers.IO) {
                getCategoriesUseCase()
            }
            when (result) {
                is Result.Success -> {
                    _categoryUiState.value = _categoryUiState.value.copy(
                        isLoading = false,
                        categories = result.data,
                        errorMessage = null
                    )
                }
                is Result.Error -> {
                    _categoryUiState.value = _categoryUiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is Result.Empty -> {
                    _categoryUiState.value = _categoryUiState.value.copy(
                        isLoading = false,
                        categories = emptyList(),
                        errorMessage = null
                    )
                }
            }
        }
    }
    fun loadProducts(
        keyword: String ?= null,
        categories: Set<String> = emptySet(),
        minPrice: Int? = null,
        maxPrice: Int? = null,
        page: Int = 1
    ) {
        _categoryUiState.value = _categoryUiState.value.copy(getProductsState = UiState.Loading)
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                getProductsUseCase.invoke(
                    keyword = keyword,
                    categories = categories,
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    page = page
                )
            }
            when (result) {
                is Result.Success -> {
                    _categoryUiState.value = _categoryUiState.value.copy(getProductsState = UiState.Success(result.data))
                }

                is Result.Error -> {
                    _categoryUiState.value = _categoryUiState.value.copy(getProductsState = UiState.Error(result.message))
                }
                is Result.Empty -> {
                }
            }
        }
    }



}