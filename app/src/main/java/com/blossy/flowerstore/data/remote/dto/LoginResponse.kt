package com.blossy.flowerstore.data.remote.dto

import com.blossy.flowerstore.domain.model.User

data class LoginResponse (
    val token: String,
    val user: User
)