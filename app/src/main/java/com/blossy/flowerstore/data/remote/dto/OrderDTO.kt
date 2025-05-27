package com.blossy.flowerstore.data.remote.dto

import com.blossy.flowerstore.domain.model.PaymentResultModel
import com.google.gson.annotations.SerializedName

data class OrderDTO (
    @SerializedName("_id")
    val id: String,
    @SerializedName("user")
    val user: String?,
    @SerializedName("orderItems")
    val orderItems: List<OrderItemDTO>,
    @SerializedName("shippingAddress")
    val shippingAddress: ShippingAddressDTO,
    @SerializedName("paymentMethod")
    val paymentMethod: String,
    @SerializedName("shippingPrice")
    val shippingPrice: Double,
    @SerializedName("totalPrice")
    val totalPrice: Double,
    @SerializedName("isPaid")
    val isPaid: Boolean,
    @SerializedName("isDelivered")
    val isDelivered: Boolean,
    @SerializedName("paymentResult")
    val paymentResult: PaymentResultModel?,
    @SerializedName("status")
    val status: String,
    @SerializedName("createdAt")
    val createdAt: String
)