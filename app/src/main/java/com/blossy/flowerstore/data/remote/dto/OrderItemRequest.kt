package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OrderItemRequest(
    @SerializedName("product") val productId: String,
    @SerializedName("quantity") val quantity: Int
)