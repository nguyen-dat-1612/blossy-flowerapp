package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateCartDTO (
    @SerializedName("productId")
    val productId: String,
    @SerializedName("quantity")
    val quantity: Int
)