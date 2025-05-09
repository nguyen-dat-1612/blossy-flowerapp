package com.blossy.flowerstore.domain.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val images: List<String>,
    val category: Category,
    val stock: Int,
    val rating: Double,
    val numReviews: Int,
    val reviews: List<Review>,
    var isFavorite: Boolean = false
)