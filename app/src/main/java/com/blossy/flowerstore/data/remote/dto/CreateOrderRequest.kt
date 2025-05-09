package com.blossy.flowerstore.data.remote.dto

import com.blossy.flowerstore.domain.model.ShippingAddress
import com.google.gson.annotations.SerializedName

data class CreateOrderRequest(
    @SerializedName("orderItems") val orderItems: List<OrderItemRequest>,
    @SerializedName("shippingAddress") val shippingAddress: ShippingAddress,
    @SerializedName("paymentMethod") val paymentMethod: String,
)
