package com.blossy.flowerstore.domain.model.request

data class RegisterModel(
    val name: String,
    val email: String,
    val password: String
)