package com.blossy.flowerstore.data.remote.dto

import com.blossy.flowerstore.domain.model.CartItem
import com.google.gson.annotations.SerializedName

data class CartResponse  (
    @SerializedName("_id")
    val id: String,
    val user: String,
    val items: List<CartItem>,
    val updatedAt: String
)