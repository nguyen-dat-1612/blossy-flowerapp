package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AddToCartDTO(
    @SerializedName("productId")
    val productId: String,
    @SerializedName("quantity")
    val quantity: Int
)
