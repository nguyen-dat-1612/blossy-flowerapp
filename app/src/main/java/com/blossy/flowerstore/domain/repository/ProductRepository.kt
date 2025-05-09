package com.blossy.flowerstore.domain.repository

import com.blossy.flowerstore.data.remote.dto.ProductResponse
import com.blossy.flowerstore.domain.model.Product
import com.blossy.flowerstore.domain.utils.Result
interface ProductRepository {
    suspend fun getTopProducts(): Result<List<Product>>

    suspend fun searchProducts(
        keyword: String,
        categories: Set<String> = emptySet(),
        minPrice: Int? = null,
        maxPrice: Int? = null,
        page: Int = 1
    ): Result<ProductResponse>

    suspend fun getProductById(id: String): Result<Product>
}