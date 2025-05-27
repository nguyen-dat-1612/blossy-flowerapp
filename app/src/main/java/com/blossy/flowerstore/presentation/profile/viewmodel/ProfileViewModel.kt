package com.blossy.flowerstore.presentation.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.model.UpdateProfileModel
import com.blossy.flowerstore.domain.usecase.user.GetUseProfileUseCase
import com.blossy.flowerstore.domain.usecase.user.UpdateUserProfileUseCase
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.profile.state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUseProfileUseCase: GetUseProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase
) : ViewModel() {
    private val _profileUiState = MutableStateFlow(ProfileUiState())
    val profileUiState: StateFlow<ProfileUiState> = _profileUiState

    fun getUserProfile() {
        _profileUiState.value = _profileUiState.value.copy(
            isLoading = true,
            errorMessage = ""
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (val result = withContext(Dispatchers.IO) { getUseProfileUseCase() }) {
                is Result.Success -> {
                    _profileUiState.value = _profileUiState.value.copy(
                        isLoading = false,
                        user = result.data
                    )
                }
                is Result.Error -> {
                    _profileUiState.value = _profileUiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                else -> Unit
            }

        }

    }

    fun updateUserProfile(id: String, name: String, email: String) {
        _profileUiState.value = _profileUiState.value.copy(updateProfileState = UiState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = updateUserProfileUseCase(id, UpdateProfileModel(name, email))) {
                is Result.Success -> {
                    _profileUiState.value = _profileUiState.value.copy(
                        user = result.data,
                        updateProfileState = UiState.Success(true)
                    )
                }

                is Result.Error -> {
                    _profileUiState.value = _profileUiState.value.copy(
                        updateProfileState = UiState.Error(result.message)
                    )
                }

                else -> {}
            }
        }
    }
}