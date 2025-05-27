package com.blossy.flowerstore.presentation.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.data.remote.dto.LoginResponse
import com.blossy.flowerstore.domain.model.request.LoginModel
import com.blossy.flowerstore.domain.model.response.LoginResponseModel
import com.blossy.flowerstore.domain.usecase.auth.LoginUseCase
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<LoginResponseModel>>(UiState.Idle)
    val uiState: StateFlow<UiState<LoginResponseModel>> = _uiState

    fun login(email: String, password: String) {
        _uiState.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val result = withContext(Dispatchers.IO) {
                loginUseCase(LoginModel(email, password))
            }
            when(result) {
                is Result.Success -> _uiState.value = UiState.Success(result.data)
                is Result.Error -> _uiState.value = UiState.Error(result.message)
                is Result.Empty -> {}
            }
        }
    }
}