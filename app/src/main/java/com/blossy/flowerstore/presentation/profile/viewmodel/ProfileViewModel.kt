package com.blossy.flowerstore.presentation.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.model.User
import com.blossy.flowerstore.domain.usecase.user.GetUseProfileUseCase
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUseProfileUseCase: GetUseProfileUseCase
) : ViewModel() {
    private val _userProfileUiState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val userProfileUiState: StateFlow<UiState<User>> = _userProfileUiState

    fun getUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            _userProfileUiState.value = UiState.Loading

            when (val result = getUseProfileUseCase()) {
                is Result.Success -> {
                    _userProfileUiState.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _userProfileUiState.value = UiState.Error(result.message)
                }
                else -> {}
            }

        }

    }
}