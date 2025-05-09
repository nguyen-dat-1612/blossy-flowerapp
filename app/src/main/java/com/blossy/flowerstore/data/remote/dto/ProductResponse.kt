package com.blossy.flowerstore.data.remote.dto

import com.blossy.flowerstore.domain.model.Pagination
import com.blossy.flowerstore.domain.model.Product


data class ProductResponse (
    val count: Int,
    val total: Int,
    val pagination: Pagination,
    val products: List<Product>
)