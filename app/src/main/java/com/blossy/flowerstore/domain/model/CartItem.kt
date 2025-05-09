package com.blossy.flowerstore.domain.model

import com.google.gson.annotations.SerializedName

data class CartItem(
    val product: Product,
    val quantity: Int,
    @SerializedName("_id")
    val id: String
)