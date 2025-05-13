package com.blossy.flowerstore.data.remote.dto

data class UpdatePasswordRequest (
    val oldPassword: String,
    val newPassword: String
)