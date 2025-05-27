package com.blossy.flowerstore.data.remote.dto

data class UpdatePasswordDTO (
    val oldPassword: String,
    val newPassword: String
)