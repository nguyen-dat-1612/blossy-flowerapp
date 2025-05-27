package com.blossy.flowerstore.domain.model.request

data class UpdateCartModel (
    val productId: String,
    val quantity: Int
)