package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FavoriteResponse(
    @SerializedName("_id")
    val id: Int,
    @SerializedName("user")
    val userId: String,
    @SerializedName("product")
    val productId: String

)
