package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CategoryDTO(
    @SerializedName("_id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("productCount")
    val productCount: Int
)
