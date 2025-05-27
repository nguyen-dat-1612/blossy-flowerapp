package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName


data class ProductListDTO (
    @SerializedName("count")
    val count: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("pagination")
    val pagination: PaginationDTO,
    @SerializedName("products")
    val products: List<ProductDTO>
)