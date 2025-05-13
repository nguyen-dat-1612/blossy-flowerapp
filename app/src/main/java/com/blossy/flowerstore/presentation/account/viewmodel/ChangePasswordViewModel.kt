package com.blossy.flowerstore.presentation.account.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.usecase.auth.UpdatePasswordUseCase
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val updatePasswordUseCase: UpdatePasswordUseCase
) : ViewModel(){

    private val _updatePassword = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val updatePassword: StateFlow<UiState<Boolean>> = _updatePassword

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch (Dispatchers.IO) {
            _updatePassword.value = UiState.Loading
            when (val result = updatePasswordUseCase(oldPassword, newPassword)) {
                is Result.Success -> {
                    _updatePassword.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _updatePassword.value = UiState.Error(result.message)
                }
                else -> {}
            }

        }
    }
}