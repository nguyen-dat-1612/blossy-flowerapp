package com.blossy.flowerstore.presentation.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.model.User
import com.blossy.flowerstore.domain.usecase.user.GetUseProfileUseCase
import com.blossy.flowerstore.domain.usecase.user.UpdateUserProfileUseCase
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
    private val getUseProfileUseCase: GetUseProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase
) : ViewModel() {
    private val _userProfileUiState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val userProfileUiState: StateFlow<UiState<User>> = _userProfileUiState

    private val _updateProfileUiState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val updateProfileUiState: StateFlow<UiState<User>> = _updateProfileUiState

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

    fun updateUserProfile(id: String, name: String, email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _updateProfileUiState.value = UiState.Loading
            when (val result = updateUserProfileUseCase(id, name, email)) {
                is Result.Success -> {
                    _updateProfileUiState.value = UiState.Success(result.data)
                }

                is Result.Error -> {
                    _updateProfileUiState.value = UiState.Error(result.message)
                }

                else -> {}
            }
        }
    }
}