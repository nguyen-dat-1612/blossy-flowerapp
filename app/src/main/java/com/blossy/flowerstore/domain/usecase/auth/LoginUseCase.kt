package com.blossy.flowerstore.domain.usecase.auth

import com.blossy.flowerstore.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) =
        authRepository.login(email, password)
}