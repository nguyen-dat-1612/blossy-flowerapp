package com.blossy.flowerstore.data.remote.api

import com.blossy.flowerstore.data.remote.dto.LoginRequest
import com.blossy.flowerstore.data.remote.dto.RegisterRequest
import com.blossy.flowerstore.data.remote.dto.LoginResponse
import com.blossy.flowerstore.data.remote.utils.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {
    @Headers("No-Authentication: true")
    @POST("auth/register")
    suspend fun register( @Body request: RegisterRequest): Response<BaseResponse<LoginResponse>>

    @Headers("No-Authentication: true")
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<BaseResponse<LoginResponse>>
}