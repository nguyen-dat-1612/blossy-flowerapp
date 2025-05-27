package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDTO (
    @SerializedName("_id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("emailVerified")
    val emailVerified: Boolean,
    @SerializedName("avatar")
    val avatar: String,
)