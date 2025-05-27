package com.blossy.flowerstore.domain.repository

import com.blossy.flowerstore.data.remote.dto.LoginResponse
import com.blossy.flowerstore.domain.model.request.LoginModel
import com.blossy.flowerstore.domain.model.request.RegisterModel
import com.blossy.flowerstore.domain.model.request.UpdatePasswordModel
import com.blossy.flowerstore.domain.model.response.LoginResponseModel
import com.blossy.flowerstore.domain.utils.Result

interface AuthRepository {
    suspend fun register(registerModel: RegisterModel): Result<LoginResponseModel>

    suspend fun login(loginModel: LoginModel): Result<LoginResponseModel>

    suspend fun updatePassword(updatePasswordModel: UpdatePasswordModel): Result<Boolean>

    suspend fun logout(): Result<Boolean>

    suspend fun forgotPassword(email: String): Result<Boolean>
}