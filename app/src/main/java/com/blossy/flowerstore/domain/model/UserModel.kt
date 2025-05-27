package com.blossy.flowerstore.domain.model

data class UserModel(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val emailVerified: Boolean,
    val avatar: String
)