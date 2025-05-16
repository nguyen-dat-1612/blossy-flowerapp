package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.remote.api.AuthApi
import com.blossy.flowerstore.data.remote.dto.LoginRequest
import com.blossy.flowerstore.data.remote.dto.LoginResponse
import com.blossy.flowerstore.data.remote.dto.RegisterRequest
import com.blossy.flowerstore.data.remote.dto.UpdatePasswordRequest
import com.blossy.flowerstore.domain.repository.AuthRepository
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.utils.SecureTokenManager
import com.blossy.flowerstore.data.remote.utils.safeApiCall
import com.blossy.flowerstore.data.remote.utils.toResult
import kotlinx.coroutines.withTimeout

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val secureTokenManager: SecureTokenManager
) : AuthRepository {

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<LoginResponse> = withTimeout(TIMEOUT){
        safeApiCall {
            api.register(RegisterRequest(name, email, password)).toResult { response ->
                secureTokenManager.saveAccessToken(response.token)
                response
            }
        }
    }

    override suspend fun login(email: String, password: String): Result<LoginResponse> = withTimeout(TIMEOUT){
        safeApiCall {
            api.login(LoginRequest(email, password)).toResult { response ->
                secureTokenManager.saveAccessToken(response.token)
                response
            }
        }
    }

    override suspend fun updatePassword(oldPassword: String, newPassword: String): Result<Boolean> = withTimeout(TIMEOUT){
        safeApiCall {
            api.updatePassword(UpdatePasswordRequest(oldPassword, newPassword)).toResult()
        }
    }

    companion object {
        private const val TIMEOUT = 5000L
    }
}