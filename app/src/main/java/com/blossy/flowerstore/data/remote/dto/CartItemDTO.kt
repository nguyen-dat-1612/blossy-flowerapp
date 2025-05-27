package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CartItemDTO(
    @SerializedName("product")
    val product: ProductDTO,
    @SerializedName("quantity")
    val quantity: Int
)