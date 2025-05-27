package com.blossy.flowerstore.domain.repository

import com.blossy.flowerstore.domain.model.ProductModel
import com.blossy.flowerstore.domain.model.response.ProductListModel
import com.blossy.flowerstore.domain.utils.Result
interface ProductRepository {
    suspend fun getTopProducts(): Result<List<ProductModel>>

    suspend fun searchProducts(
        keyword: String ?,
        categories: Set<String> = emptySet(),
        minPrice: Int? = null,
        maxPrice: Int? = null,
        page: Int = 1
    ): Result<ProductListModel>

    suspend fun getProductById(id: String): Result<ProductModel>
}