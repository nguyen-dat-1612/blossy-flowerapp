package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OrderItemDTO(
    @SerializedName("product")
    val product: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("_id")
    val id: String
)