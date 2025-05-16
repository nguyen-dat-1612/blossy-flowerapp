package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.remote.api.ProductApi
import com.blossy.flowerstore.data.remote.dto.ProductResponse
import com.blossy.flowerstore.domain.model.Product
import com.blossy.flowerstore.domain.repository.ProductRepository
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.data.remote.utils.safeApiCall
import com.blossy.flowerstore.data.remote.utils.toResult
import kotlinx.coroutines.withTimeout

class ProductRepositoryImpl  @Inject constructor(
    private val productApi: ProductApi
): ProductRepository {
    override suspend fun getTopProducts(): Result<List<Product>> = withTimeout(TIMEOUT) {
        safeApiCall {
            productApi.getTopProducts().toResult()
        }
    }

    override suspend fun searchProducts(
        keyword: String?,
        categories: Set<String>,
        minPrice: Int?,
        maxPrice: Int?,
        page: Int
    ): Result<ProductResponse> = withTimeout(TIMEOUT)  {
        safeApiCall {
            productApi.getProducts(
                keyword = keyword,
                category = categories.joinToString(",").takeIf { it.isNotEmpty() },
                minPrice = minPrice,
                maxPrice = maxPrice,
                page = page
            ).toResult()
        }
    }


    override suspend fun getProductById(id: String): Result<Product> = withTimeout(TIMEOUT)  {
        safeApiCall {
            productApi.getProductById(id).toResult()
        }
    }


    companion object {
        private const val TIMEOUT = 5000L
    }

}