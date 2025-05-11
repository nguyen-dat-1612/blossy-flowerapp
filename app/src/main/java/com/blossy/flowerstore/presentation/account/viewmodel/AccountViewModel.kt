package com.blossy.flowerstore.presentation.account.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.usecase.user.UpdateFcmUseCase
import com.blossy.flowerstore.presentation.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.blossy.flowerstore.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val updateFcmUseCase: UpdateFcmUseCase
) : ViewModel() {

    private val _updateFcm = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val updateFcm : StateFlow<UiState<Boolean>> = _updateFcm

    fun updateFcmToken(token: String) {
        viewModelScope.launch (Dispatchers.IO) {
            _updateFcm.value = UiState.Loading
            when(val result = updateFcmUseCase(token)) {
                is Result.Success -> {
                    _updateFcm.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _updateFcm.value = UiState.Error(result.message)
                }
                else -> {}
            }
        }
    }

}