package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CategoryResponse (
    @SerializedName("_id")
    val id: String,
    val name: String,
    val description: String,
    val image: String,
    val productCount: Int
)