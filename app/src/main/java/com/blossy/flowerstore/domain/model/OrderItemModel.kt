package com.blossy.flowerstore.domain.model

data class OrderItemModel(
    val product: String,
    val name: String,
    val image: String,
    val price: Double,
    val quantity: Int,
    val id: String
)