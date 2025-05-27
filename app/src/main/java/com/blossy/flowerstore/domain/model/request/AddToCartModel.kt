package com.blossy.flowerstore.domain.model.request

data class AddToCartModel(
    val productId: String,
    val quantity: Int
)