package com.blossy.flowerstore.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.model.Category
import com.blossy.flowerstore.domain.model.Product
import com.blossy.flowerstore.domain.model.User
import com.blossy.flowerstore.domain.usecase.category.GetCategoriesUseCase
import com.blossy.flowerstore.domain.usecase.product.GetTopProductsUseCase
import com.blossy.flowerstore.domain.usecase.user.GetUseProfileUseCase
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getTopProductsUseCase: GetTopProductsUseCase,
    private val getUseProfileUseCase: GetUseProfileUseCase
) : ViewModel() {

    private val _categoryUiState = MutableStateFlow<UiState<List<Category>>>(UiState.Idle)
    val categoryUiState: StateFlow<UiState<List<Category>>> = _categoryUiState

    private val _productUiState = MutableStateFlow<UiState<List<Product>>>(UiState.Idle)
    val productUiState: StateFlow<UiState<List<Product>>> = _productUiState

    private val _userProfileUiState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val userProfileUiState: StateFlow<UiState<User>> = _userProfileUiState

    fun loadHomeData() {
        viewModelScope.launch(Dispatchers.IO) {

            _categoryUiState.value = UiState.Loading
            _productUiState.value = UiState.Loading
            _userProfileUiState.value = UiState.Loading

            supervisorScope {
                val catDeferred = async { getCategoriesUseCase() }
                val prodDeferred = async { getTopProductsUseCase() }
                val userDeferred = async { getUseProfileUseCase() }

                when (val catRes = catDeferred.await()) {
                    is Result.Success -> _categoryUiState.value = UiState.Success(catRes.data)
                    is Result.Error -> _categoryUiState.value = UiState.Error(catRes.message)
                    is Result.Empty -> _categoryUiState.value = UiState.Success(emptyList())
                }
                when (val prodRes = prodDeferred.await()) {
                    is Result.Success -> _productUiState.value = UiState.Success(prodRes.data)
                    is Result.Error -> _productUiState.value = UiState.Error(prodRes.message)
                    is Result.Empty -> _productUiState.value = UiState.Success(emptyList())
                }
                when (val userRes = userDeferred.await()) {
                    is Result.Success -> _userProfileUiState.value = UiState.Success(userRes.data)
                    is Result.Error -> _userProfileUiState.value = UiState.Error(userRes.message)
                    else -> _userProfileUiState.value = UiState.Error("No data")
                }
            }
        }
    }
}
