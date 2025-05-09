package com.blossy.flowerstore.data.remote.dto

data class AddtoCartRequest(
    val productId: String,
    val quantity: Int
)
