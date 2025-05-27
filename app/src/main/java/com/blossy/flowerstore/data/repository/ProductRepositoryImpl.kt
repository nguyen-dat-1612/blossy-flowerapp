package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.mapper.toListProduct
import com.blossy.flowerstore.data.mapper.toProduct
import com.blossy.flowerstore.data.remote.api.ProductApi
import com.blossy.flowerstore.domain.repository.ProductRepository
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.data.remote.utils.safeApiCall
import com.blossy.flowerstore.data.remote.utils.toResult
import com.blossy.flowerstore.domain.model.ProductModel
import com.blossy.flowerstore.domain.model.response.ProductListModel
import kotlinx.coroutines.withTimeout

class ProductRepositoryImpl  @Inject constructor(
    private val productApi: ProductApi
): ProductRepository {
    override suspend fun getTopProducts(): Result<List<ProductModel>>  {
        return safeApiCall {
            productApi.getTopProducts().let {
                it.toResult { dtoList ->
                    dtoList.map { it.toProduct() }
                }
            }
        }
    }

    override suspend fun searchProducts(
        keyword: String?,
        categories: Set<String>,
        minPrice: Int?,
        maxPrice: Int?,
        page: Int
    ): Result<ProductListModel>  {
        return safeApiCall {
            productApi.getProducts(
                keyword = keyword,
                category = categories.joinToString(",").takeIf { it.isNotEmpty() },
                minPrice = minPrice,
                maxPrice = maxPrice,
                page = page
            ).toResult {
                it.toListProduct()
            }
        }
    }


    override suspend fun getProductById(id: String): Result<ProductModel>  {
        return safeApiCall {
            productApi.getProductById(id).let {
                it.toResult { dto ->
                    dto.toProduct()
                }
            }
        }
    }



}