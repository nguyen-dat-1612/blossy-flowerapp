package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ForgotPasswordDTO(
    @SerializedName("email")
    val email: String
)
