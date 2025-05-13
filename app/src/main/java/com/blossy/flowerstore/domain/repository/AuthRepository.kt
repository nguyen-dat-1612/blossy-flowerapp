package com.blossy.flowerstore.domain.repository

import com.blossy.flowerstore.data.remote.dto.LoginResponse
import com.blossy.flowerstore.domain.utils.Result

interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): Result<LoginResponse>

    suspend fun login(email: String, password: String): Result<LoginResponse>

    suspend fun updatePassword(oldPassword: String, newPassword: String): Result<Boolean>

}