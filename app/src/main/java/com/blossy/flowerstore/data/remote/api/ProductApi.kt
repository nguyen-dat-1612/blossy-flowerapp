package com.blossy.flowerstore.data.remote.api

import com.blossy.flowerstore.data.remote.dto.ProductDTO
import com.blossy.flowerstore.data.remote.dto.ProductListDTO
import com.blossy.flowerstore.data.remote.utils.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
interface ProductApi {
    @Headers("No-Authentication: true")
    @GET("products/top-products")
    suspend fun getTopProducts(): BaseResponse<List<ProductDTO>>

    @Headers("No-Authentication: true")
    @GET("products")
    suspend fun getProducts(
        @Query("keyword") keyword: String?,
        @Query("category") category: String?,
        @Query("minPrice") minPrice: Int?,
        @Query("maxPrice") maxPrice: Int?,
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10
    ): BaseResponse<ProductListDTO>

    @Headers("No-Authentication: true")
    @GET("products/{id}")
    suspend fun getProductById(
        @Path ("id") id: String
    ) : BaseResponse<ProductDTO>

}