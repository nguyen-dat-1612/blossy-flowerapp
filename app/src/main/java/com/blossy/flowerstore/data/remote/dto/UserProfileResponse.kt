package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserProfileResponse (
    @SerializedName("_id")
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val emailVerified: Boolean,
    val avatar: String,
)