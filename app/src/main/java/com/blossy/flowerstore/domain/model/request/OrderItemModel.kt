package com.blossy.flowerstore.domain.model.request

data class OrderItemModel(
    val productId: String,
    val quantity: Int
)