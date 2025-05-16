package com.blossy.flowerstore.presentation.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.model.Product
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
import kotlinx.coroutines.delay

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteUseCase: GetFavoriteUseCase,
//    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase
): ViewModel() {
    private val _favoriteProducts = MutableStateFlow<UiState<List<Product>>>(UiState.Idle)
    val favoriteProducts: StateFlow<UiState<List<Product>>> = _favoriteProducts

    private val _toggleFavoriteState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val toggleFavoriteState: StateFlow<UiState<Boolean>> = _toggleFavoriteState

    fun loadFavoriteProducts() {
        viewModelScope.launch (Dispatchers.IO){
            _favoriteProducts.value = UiState.Loading
            when (val result = getFavoriteUseCase()) {
                is Result.Success -> {
                    _favoriteProducts.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _favoriteProducts.value = UiState.Error(result.message)
                }
                else -> {}
            }
        }
    }

    fun toggleFavorite(productId: String) {
        viewModelScope.launch (Dispatchers.IO){
            _toggleFavoriteState.value = UiState.Loading
            when (val result = removeFavoriteUseCase(productId)) {
                is Result.Success -> {
                    updateLocalListAfterRemoval(productId)
                    _toggleFavoriteState.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _toggleFavoriteState.value = UiState.Error(result.message)
                }
                else -> {}
            }
            delay(500)
            _toggleFavoriteState.value = UiState.Idle
        }
    }

    fun toggleFavoriteState(state: UiState<Boolean>) {
        _toggleFavoriteState.value = state
    }

    private fun updateLocalListAfterRemoval(removedProductId: String) {
        val currentList = (_favoriteProducts.value as? UiState.Success)?.data ?: return
        val updatedList = currentList.filter { it.id != removedProductId }
        _favoriteProducts.value = UiState.Success(updatedList)
    }
}