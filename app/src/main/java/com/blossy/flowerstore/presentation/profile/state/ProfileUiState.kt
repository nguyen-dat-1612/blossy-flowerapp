package com.blossy.flowerstore.presentation.profile.state

import com.blossy.flowerstore.domain.model.UserModel
import com.blossy.flowerstore.presentation.common.UiState

data class ProfileUiState (
    val isLoading: Boolean = false,
    val user: UserModel? = null,
    val errorMessage: String = "",
    val updateProfileState: UiState<Boolean> = UiState.Idle
)