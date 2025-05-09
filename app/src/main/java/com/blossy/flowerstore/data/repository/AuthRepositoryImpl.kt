package com.blossy.flowerstore.data.repository

import android.util.Log
import com.blossy.flowerstore.data.remote.api.AuthApi
import com.blossy.flowerstore.data.remote.dto.LoginRequest
import com.blossy.flowerstore.data.remote.dto.LoginResponse
import com.blossy.flowerstore.data.remote.dto.RegisterRequest
import com.blossy.flowerstore.domain.repository.AuthRepository
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.utils.SecureTokenManager

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val secureTokenManager: SecureTokenManager
) : AuthRepository {

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<LoginResponse> {
        return try {
            val response = api.register(RegisterRequest(name, email, password))
            if (response.isSuccessful) {
                val body = response.body()
                Log.d("register", body.toString())
                if (body != null && body.success && body.code == 200 && body.data != null) {
                    secureTokenManager.saveAccessToken(body.data.token)
                    Result.Success(body.data)
                } else {
                    Result.Error(body?.message ?: "Unknown server error")
                }
            } else {
                Result.Error(response.errorBody()?.string() ?: "Network error")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown exception")
        }
    }

    override suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()
                Log.d("login", body.toString())
                if (body != null && body.success && body.code == 200 && body.data != null) {
                    secureTokenManager.saveAccessToken(body.data.token)
                    Result.Success(body.data)
                } else {
                    Result.Error(body?.message ?: "Unknown server error")
                }
            }
            else {
                Result.Error(response.errorBody()?.string() ?: "Network error")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown exception")
        }
    }

}