package com.blossy.flowerstore.data.remote.utils

data class BaseResponse<T>(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: T?
)
