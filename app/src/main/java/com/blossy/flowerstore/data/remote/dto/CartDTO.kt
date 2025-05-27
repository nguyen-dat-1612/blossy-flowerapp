package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CartDTO  (
    @SerializedName("_id")
    val id: String,
    @SerializedName("user")
    val user: String,
    @SerializedName("items")
    val items: List<CartItemDTO>,
    @SerializedName("updatedAt")
    val updatedAt: String
)