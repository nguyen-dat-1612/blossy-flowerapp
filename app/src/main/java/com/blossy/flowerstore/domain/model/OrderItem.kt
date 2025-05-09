package com.blossy.flowerstore.domain.model

data class OrderItem(
    val product: String,
    val name: String,
    val image: String,
    val price: Double,
    val quantity: Int,
    val _id: String
)