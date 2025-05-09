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
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteUseCase: GetFavoriteUseCase,
//    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase
): ViewModel() {
    private val _favoriteProducts = MutableStateFlow<UiState<List<Product>>>(UiState.Idle)
    val favoriteProducts: StateFlow<UiState<List<Product>>> = _favoriteProducts

    private val _isFavorite = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val isFavorite: StateFlow<UiState<Boolean>> = _isFavorite

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

    fun isFavorite(productId: String) {
        viewModelScope.launch (Dispatchers.IO){
            _isFavorite.value = UiState.Loading
            when (val result = removeFavoriteUseCase(productId)) {
                is Result.Success -> {
                    updateLocalListAfterRemoval(productId)
                    _isFavorite.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _isFavorite.value = UiState.Error(result.message)
                }
                else -> {}
            }
        }
    }

    private fun updateLocalListAfterRemoval(removedProductId: String) {
        val currentList = (_favoriteProducts.value as? UiState.Success)?.data ?: return
        val updatedList = currentList.filter { it.id != removedProductId }
        _favoriteProducts.value = UiState.Success(updatedList)
    }
}