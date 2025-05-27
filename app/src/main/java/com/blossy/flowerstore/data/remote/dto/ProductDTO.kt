package com.blossy.flowerstore.data.remote.dto
import com.google.gson.annotations.SerializedName

data class ProductDTO(
    @SerializedName("_id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("category")
    val category: CategoryDTO,
    @SerializedName("stock")
    val stock: Int,
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("numReviews")
    val numReviews: Int,
    @SerializedName("reviews")
    val reviews: List<ReviewDTO>,
    @SerializedName("createdAt")
    val createdAt: String
)
