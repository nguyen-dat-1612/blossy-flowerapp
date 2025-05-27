package com.blossy.flowerstore.presentation.account.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.model.request.UpdatePasswordModel
import com.blossy.flowerstore.domain.usecase.auth.UpdatePasswordUseCase
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result
import kotlinx.coroutines.withContext

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val updatePasswordUseCase: UpdatePasswordUseCase
) : ViewModel(){

    private val _updatePassword = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val updatePassword: StateFlow<UiState<Boolean>> = _updatePassword

    fun changePassword(oldPassword: String, newPassword: String) {
        _updatePassword.value = UiState.Loading
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { updatePasswordUseCase(UpdatePasswordModel(oldPassword, newPassword)) }
            when (result) {
                is Result.Success -> { _updatePassword.value = UiState.Success(result.data) }
                is Result.Error -> { _updatePassword.value = UiState.Error(result.message) }
                else -> {}
            }
        }
    }
}