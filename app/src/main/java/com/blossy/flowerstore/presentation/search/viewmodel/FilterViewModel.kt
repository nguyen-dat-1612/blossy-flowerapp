package com.blossy.flowerstore.presentation.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.model.Category
import com.blossy.flowerstore.domain.usecase.category.GetCategoriesUseCase
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _categoryUiState = MutableStateFlow<UiState<List<Category>>>(UiState.Idle)
    val categoryUiState: StateFlow<UiState<List<Category>>> = _categoryUiState

    private val _selectedCategories = MutableStateFlow<Set<String>>(emptySet())
    val selectedCategories: StateFlow<Set<String>> = _selectedCategories

    private val _priceRange = MutableStateFlow<Pair<Int, Int>>(0 to 1000000) // Giá trị mặc định
    val priceRange: StateFlow<Pair<Int, Int>> = _priceRange

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        getCategories()
        getPriceRange()
    }

    fun getCategories() {
        viewModelScope.launch {
            _categoryUiState.value = UiState.Loading
            when (val result = getCategoriesUseCase()) {
                is Result.Success -> {
                    _categoryUiState.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _categoryUiState.value = UiState.Error(result.message)
                }
                is Result.Empty -> {
                    _categoryUiState.value = UiState.Success(emptyList())
                }
            }
        }
    }

    fun getPriceRange() {
        viewModelScope.launch {
            try {
                _priceRange.value = 0 to 2000000
            } catch (e: Exception) {
                // Giữ nguyên giá trị mặc định nếu có lỗi
            }
        }
    }

    fun toggleCategorySelection(categoryId: String) {
        val newSelection = _selectedCategories.value.toMutableSet().apply {
            if (contains(categoryId)) remove(categoryId) else add(categoryId)
        }
        _selectedCategories.value = newSelection
    }

    fun resetFilters() {
        _selectedCategories.value = emptySet()
        getPriceRange() // Reset về giá trị ban đầu
    }
}