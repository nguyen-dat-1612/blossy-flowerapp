package com.blossy.flowerstore.data.remote.dto

data class RegisterRequest (
    val name: String,
    val email: String,
    val password: String,
)