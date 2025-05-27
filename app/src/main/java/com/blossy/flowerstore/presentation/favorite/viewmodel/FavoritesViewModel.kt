package com.blossy.flowerstore.presentation.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.usecase.favorite.GetFavoriteUseCase
import com.blossy.flowerstore.domain.usecase.favorite.RemoveFavoriteUseCase
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.presentation.favorite.state.FavoritesUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteUseCase: GetFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase
): ViewModel() {

    private val _favoriteUiState = MutableStateFlow(FavoritesUiState())
    val favoriteUiState: StateFlow<FavoritesUiState> = _favoriteUiState

    fun loadFavoriteProducts() {
        _favoriteUiState.value = _favoriteUiState.value.copy(
            isLoading = true,
            errorMessage = null
        )
        viewModelScope.launch {
            when (val result = withContext(Dispatchers.IO) { getFavoriteUseCase() }) {
                is Result.Success -> {
                    _favoriteUiState.value = _favoriteUiState.value.copy(
                        productItems = result.data,
                        isLoading = false
                    )
                }

                is Result.Error -> {
                    _favoriteUiState.value = _favoriteUiState.value.copy(
                        errorMessage = result.message,
                        isLoading = false
                    )
                }

                is Result.Empty -> {
                    _favoriteUiState.value = _favoriteUiState.value.copy(
                        productItems = emptyList(),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleFavorite(productId: String) {
        _favoriteUiState.value = _favoriteUiState.value.copy(toggleFavoriteState = UiState.Loading)

        viewModelScope.launch {
            when (val result = withContext(Dispatchers.IO) { removeFavoriteUseCase(productId) }) {
                is Result.Success -> {
                    val updatedList = _favoriteUiState.value.productItems.filter { it.id != productId }
                    _favoriteUiState.value = _favoriteUiState.value.copy(
                        productItems = updatedList,
                        toggleFavoriteState = UiState.Success(true)
                    )
                }

                is Result.Error -> {
                    _favoriteUiState.value = _favoriteUiState.value.copy(
                        toggleFavoriteState = UiState.Error(result.message)
                    )
                }

                else -> {}
            }

            delay(500)
            _favoriteUiState.value = _favoriteUiState.value.copy(toggleFavoriteState = UiState.Idle)
        }
    }
}