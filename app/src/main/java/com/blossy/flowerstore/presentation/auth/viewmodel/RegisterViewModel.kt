package com.blossy.flowerstore.presentation.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.data.remote.dto.LoginResponse
import com.blossy.flowerstore.domain.model.request.RegisterModel
import com.blossy.flowerstore.domain.model.response.LoginResponseModel
import com.blossy.flowerstore.domain.usecase.auth.RegisterUseCase
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<LoginResponseModel>>(UiState.Idle)
    val uiState: StateFlow<UiState<LoginResponseModel>> = _uiState


    fun register(name: String, email: String, password: String) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                registerUseCase(RegisterModel(name, email, password))
            }
            when (result) {
                is Result.Success -> _uiState.value = UiState.Success(result.data)
                is Result.Error -> _uiState.value = UiState.Error(result.message)
                is Result.Empty -> {}
            }
        }
    }
}