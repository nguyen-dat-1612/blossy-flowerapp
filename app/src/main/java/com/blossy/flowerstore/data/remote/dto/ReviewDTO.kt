package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReviewDTO (
    @SerializedName("userId")
    val userId: String,
    @SerializedName("rating")
    val rating: Int,
    @SerializedName("comment")
    val comment: String
)