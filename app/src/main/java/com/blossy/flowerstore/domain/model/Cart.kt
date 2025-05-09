package com.blossy.flowerstore.domain.model

import com.google.gson.annotations.SerializedName

data class Cart(
    @SerializedName("_id")
    val id: String,
    val user: String,
    val items: List<CartItem>,
    val updatedAt: String,
)
