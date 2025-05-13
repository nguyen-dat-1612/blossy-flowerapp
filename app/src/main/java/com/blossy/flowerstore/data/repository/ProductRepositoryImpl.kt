package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.remote.api.ProductApi
import com.blossy.flowerstore.data.remote.dto.ProductResponse
import com.blossy.flowerstore.domain.model.Product
import com.blossy.flowerstore.domain.repository.ProductRepository
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result
class ProductRepositoryImpl  @Inject constructor(
    private val productApi: ProductApi
): ProductRepository {
    override suspend fun getTopProducts(): Result<List<Product>> {
        return try {
            val response = productApi.getTopProducts()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body?.success == true && body.data != null) {
                    Result.Success(body.data)
                } else {
                    Result.Error(body?.message ?: "An error occurred")
                }
            } else {
                Result.Error(response.message())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun searchProducts(
        keyword: String?,
        categories: Set<String>,
        minPrice: Int?,
        maxPrice: Int?,
        page: Int
    ): Result<ProductResponse> {
        return try {
            val response = productApi.getProducts(
                keyword = keyword,
                category = categories.joinToString(",").takeIf { it.isNotEmpty() },
                minPrice = minPrice,
                maxPrice = maxPrice,
                page = page
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body?.success == true && body.data != null) {
                    Result.Success(body.data)
                } else {
                    Result.Error(body?.message ?: "An error occurred")
                }
            } else {
                Result.Error(response.message())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }


    override suspend fun getProductById(id: String): Result<Product> {
        return try {
            val response = productApi.getProductById(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body?.success == true && body.data != null) {
                    Result.Success(body.data)
                } else {
                    Result.Error(body?.message ?: "An error occurred")
                }
            } else {
                Result.Error(response.message())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

}