package com.blossy.flowerstore.domain.model.response

import com.blossy.flowerstore.domain.model.UserModel

data class LoginResponseModel(
    val token: String,
    val user: UserModel
)