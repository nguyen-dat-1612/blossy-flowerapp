package com.blossy.flowerstore.domain.model.request

data class CartOrderModel (
    val productId: String,
    val quantity: Int
)