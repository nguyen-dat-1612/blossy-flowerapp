package com.blossy.flowerstore.domain.model

data class ProductModel(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val images: List<String>,
    val category: CategoryModel,
    val stock: Int,
    val rating: Double,
    val numReviews: Int,
    val reviews: List<ReviewModel>,
    var isFavorite: Boolean = false
)