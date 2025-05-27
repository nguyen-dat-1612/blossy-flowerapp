package com.blossy.flowerstore.domain.model

data class OrderModel (
    val user: String?,
    val orderItems: List<OrderItemModel>,
    val shippingAddress: ShippingAddressModel,
    val paymentMethod: String,
    val shippingPrice: Double,
    val totalPrice: Double,
    val isPaid: Boolean,
    val isDelivered: Boolean,
    val paymentResult: PaymentResultModel?,
    val status: String,
    val id: String,
    val createdAt: String
)