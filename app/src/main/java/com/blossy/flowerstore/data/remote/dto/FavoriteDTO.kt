package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FavoriteDTO(
    @SerializedName("_id")
    val id: String,
    @SerializedName("user")
    val userId: String,
    @SerializedName("product")
    val productId: String

)
