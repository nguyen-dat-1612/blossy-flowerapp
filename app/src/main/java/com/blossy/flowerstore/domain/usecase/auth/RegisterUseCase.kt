package com.blossy.flowerstore.domain.usecase.auth

import com.blossy.flowerstore.data.remote.dto.LoginResponse
import com.blossy.flowerstore.domain.repository.AuthRepository
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String
    ): Result<LoginResponse> {
        return authRepository.register(name, email, password)
    }
}