package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginResponse (
    @SerializedName("token")
    val token: String,
    @SerializedName("user")
    val user: UserDTO
)