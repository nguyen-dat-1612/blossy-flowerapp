package com.blossy.flowerstore.presentation.topProducts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.usecase.product.GetTopProductsUseCase
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.presentation.topProducts.ui.PopularUiState
import kotlinx.coroutines.withContext

@HiltViewModel
class PopularViewModel @Inject constructor(
    private val getTopProductsUseCase: GetTopProductsUseCase
) : ViewModel() {

    private val _popularUiState = MutableStateFlow(PopularUiState())
    val popularUiState: StateFlow<PopularUiState> = _popularUiState

    fun fetchPopularProducts() {
        _popularUiState.value = PopularUiState(isLoading = true)
        viewModelScope.launch {
            when (val result = withContext(Dispatchers.IO) { getTopProductsUseCase() }) {
                is Result.Success -> {
                    _popularUiState.value = PopularUiState(productItems = result.data)
                }
                is Result.Error -> {
                    _popularUiState.value = PopularUiState(error = result.message)
                }
                is Result.Empty -> {
                    _popularUiState.value = PopularUiState()
                }
            }
        }
    }
}