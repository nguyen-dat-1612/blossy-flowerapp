package com.blossy.flowerstore.data.remote.api

import com.blossy.flowerstore.data.remote.dto.ForgotPasswordDTO
import com.blossy.flowerstore.data.remote.dto.LoginRequest
import com.blossy.flowerstore.data.remote.dto.RegisterDTO
import com.blossy.flowerstore.data.remote.dto.LoginResponse
import com.blossy.flowerstore.data.remote.dto.UpdatePasswordDTO
import com.blossy.flowerstore.data.remote.utils.BaseResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {
    @Headers("No-Authentication: true")
    @POST("auth/register")
    suspend fun register( @Body request: RegisterDTO): BaseResponse<LoginResponse>

    @Headers("No-Authentication: true")
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): BaseResponse<LoginResponse>

    @PUT("auth/update-password")
    suspend fun updatePassword(@Body request: UpdatePasswordDTO): BaseResponse<Boolean>

    @POST("auth/logout")
    suspend fun logout(): BaseResponse<Boolean>

    @Headers("No-Authentication: true")
    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordDTO): BaseResponse<Boolean>


}