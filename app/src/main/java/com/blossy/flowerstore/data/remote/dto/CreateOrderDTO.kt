package com.blossy.flowerstore.data.remote.dto

import com.blossy.flowerstore.domain.model.ShippingAddressModel
import com.google.gson.annotations.SerializedName

data class CreateOrderDTO(
    @SerializedName("orderItems") val orderItems: List<CartOrderDTO>,
    @SerializedName("shippingAddress") val shippingAddress: ShippingAddressDTO,
    @SerializedName("paymentMethod") val paymentMethod: String,
)
