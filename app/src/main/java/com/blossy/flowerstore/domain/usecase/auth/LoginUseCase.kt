package com.blossy.flowerstore.domain.usecase.auth

import com.blossy.flowerstore.data.remote.dto.LoginResponse
import com.blossy.flowerstore.domain.model.request.LoginModel
import com.blossy.flowerstore.domain.repository.AuthRepository
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(loginModel: LoginModel) =
        authRepository.login(loginModel)
}