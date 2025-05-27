package com.blossy.flowerstore.domain.usecase.auth

import com.blossy.flowerstore.domain.model.request.UpdatePasswordModel
import com.blossy.flowerstore.domain.repository.AuthRepository
import javax.inject.Inject

class UpdatePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(updatePasswordModel: UpdatePasswordModel) =
        authRepository.updatePassword(updatePasswordModel)
}