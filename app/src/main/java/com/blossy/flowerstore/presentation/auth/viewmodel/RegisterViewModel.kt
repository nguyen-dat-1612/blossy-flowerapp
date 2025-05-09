package com.blossy.flowerstore.presentation.auth.viewmodel

import com.blossy.flowerstore.data.remote.dto.LoginResponse
import com.blossy.flowerstore.domain.usecase.auth.RegisterUseCase
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.presentation.common.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : BaseViewModel<LoginResponse>() {

    fun register(name: String, email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _uiState.value = UiState.Loading
            when (val result = registerUseCase(name, email, password)) {
                is Result.Success -> _uiState.value = UiState.Success(result.data)
                is Result.Error -> _uiState.value = UiState.Error(result.message)
                is Result.Empty -> {}
            }
        }
    }
}