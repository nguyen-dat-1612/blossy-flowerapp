package com.blossy.flowerstore.data.repository

import android.util.Log
import com.blossy.flowerstore.data.mapper.toLoginResponse
import com.blossy.flowerstore.data.mapper.toRequest
import com.blossy.flowerstore.data.remote.api.AuthApi
import com.blossy.flowerstore.data.remote.dto.ForgotPasswordDTO
import com.blossy.flowerstore.domain.repository.AuthRepository
import javax.inject.Inject
import com.blossy.flowerstore.utils.manager.SecureTokenManager
import com.blossy.flowerstore.data.remote.utils.safeApiCall
import com.blossy.flowerstore.data.remote.utils.toResult
import com.blossy.flowerstore.domain.model.request.LoginModel
import com.blossy.flowerstore.domain.model.request.RegisterModel
import com.blossy.flowerstore.domain.model.request.UpdatePasswordModel
import com.blossy.flowerstore.domain.model.response.LoginResponseModel
import com.blossy.flowerstore.domain.utils.Result

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val secureTokenManager: SecureTokenManager
) : AuthRepository {

    override suspend fun register(
        registerModel: RegisterModel
    ): Result<LoginResponseModel> {
        Log.d("Debug", "Bắt đầu gọi API")
        return safeApiCall {
            val request = registerModel.toRequest()
            api.register(request).toResult() {
                secureTokenManager.saveAccessToken(it.token)
                it.toLoginResponse()
            }
        }
    }

    override suspend fun login(loginModel: LoginModel): Result<LoginResponseModel> {
        return safeApiCall {
            val request = loginModel.toRequest()
            api.login(request).toResult {
                secureTokenManager.saveAccessToken(it.token)
                it.toLoginResponse()
            }
        }
    }
    override suspend fun updatePassword(updatePasswordModel: UpdatePasswordModel):Result<Boolean> {
        return safeApiCall {
            val request = updatePasswordModel.toRequest();
            api.updatePassword(request).toResult()
        }
    }

    override suspend fun logout():  Result<Boolean> {
        return safeApiCall {
            api.logout().toResult().let {
                secureTokenManager.clearTokens()
                it
            }
        }

    }

    override suspend fun forgotPassword(email: String) : Result<Boolean> {
        return safeApiCall {
            api.forgotPassword(ForgotPasswordDTO(email = email)).toResult()
        }
    }

}