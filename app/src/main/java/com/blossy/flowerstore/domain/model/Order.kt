package com.blossy.flowerstore.domain.model

import com.google.gson.annotations.SerializedName

data class Order (
    val user: String,
    val orderItems: List<OrderItem>,
    val shippingAddress: ShippingAddress,
    val paymentMethod: String,
    val shippingPrice: Double,
    val totalPrice: Double,
    val isPaid: Boolean,
    val isDelivered: Boolean,
    val status: String,
    @SerializedName("_id")
    val id: String,
    val createdAt: String
)