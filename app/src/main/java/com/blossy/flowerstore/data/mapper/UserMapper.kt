package com.blossy.flowerstore.data.mapper

import com.blossy.flowerstore.data.remote.dto.UserProfileResponse
import com.blossy.flowerstore.domain.model.User

fun UserProfileResponse.toUser(): User {
    return User(
        id = id,
        name = name,
        email = email,
        role = role,
        emailVerified = emailVerified,
        avatar = avatar,
    )
}