package com.blossy.flowerstore.presentation.auth.viewmodel

import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.data.remote.dto.LoginResponse
import com.blossy.flowerstore.domain.usecase.auth.LoginUseCase
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.presentation.common.BaseViewModel
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : BaseViewModel<LoginResponse>() {

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = UiState.Loading
            when(val result = loginUseCase.invoke(email, password)) {
                is Result.Success -> _uiState.value = UiState.Success(result.data)
                is Result.Error -> _uiState.value = UiState.Error(result.message)
                is Result.Empty -> {}
            }
        }
    }
}