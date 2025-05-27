package com.blossy.flowerstore.domain.usecase.auth

import com.blossy.flowerstore.domain.repository.AuthRepository
import javax.inject.Inject

class ForgotUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String) = authRepository.forgotPassword(email)
}