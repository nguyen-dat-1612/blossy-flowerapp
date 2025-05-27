package com.blossy.flowerstore.presentation.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.usecase.auth.ForgotUseCase
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result

@HiltViewModel
class ForgotViewModel @Inject constructor(
    private val forgotUseCase: ForgotUseCase
) : ViewModel() {
    private val _forgotPassword = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val forgotPassword: StateFlow<UiState<Boolean>> = _forgotPassword

    fun forgotPassword(email: String) {
        _forgotPassword.value = UiState.Loading
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                forgotUseCase(email)
            }
            when (result) {
                is Result.Success -> _forgotPassword.value = UiState.Success(result.data)
                is Result.Error -> _forgotPassword.value = UiState.Error(result.message)
                else -> Unit
            }
        }
    }
}