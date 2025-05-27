package com.blossy.flowerstore.presentation.account.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.usecase.auth.LogoutUseCase
import com.blossy.flowerstore.domain.usecase.user.UpdateFcmUseCase
import com.blossy.flowerstore.presentation.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.blossy.flowerstore.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val updateFcmUseCase: UpdateFcmUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _updateFcm = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val updateFcm : StateFlow<UiState<Boolean>> = _updateFcm

    private val _logout = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val logout : StateFlow<UiState<Boolean>> = _logout


    fun updateFcmToken(token: String) {
        _updateFcm.value = UiState.Loading
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                updateFcmUseCase(token)
            }
            when (result) {
                is Result.Success -> _updateFcm.value = UiState.Success(result.data)
                is Result.Error -> _updateFcm.value = UiState.Error(result.message)
                else -> Unit
            }
        }
    }

    fun logout() {
        _logout.value = UiState.Loading
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                logoutUseCase()
            }
            when (result) {
                is Result.Success -> _logout.value = UiState.Success(result.data)
                is Result.Error -> _logout.value = UiState.Error(result.message)
                else -> Unit
            }
        }
    }

}