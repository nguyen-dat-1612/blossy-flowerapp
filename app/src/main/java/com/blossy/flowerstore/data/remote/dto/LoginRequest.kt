package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest (
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)