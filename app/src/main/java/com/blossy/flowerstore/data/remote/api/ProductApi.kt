package com.blossy.flowerstore.data.remote.api

import com.blossy.flowerstore.data.remote.dto.ProductResponse
import com.blossy.flowerstore.data.remote.utils.BaseResponse
import com.blossy.flowerstore.domain.model.Product
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
interface ProductApi {
    @Headers("No-Authentication: true")
    @GET("products/top-products")
    suspend fun getTopProducts(): Response<BaseResponse<List<Product>>>

    @Headers("No-Authentication: true")
    @GET("products")
    suspend fun getProducts(
        @Query("keyword") keyword: String?,
        @Query("category") category: String?,
        @Query("minPrice") minPrice: Int?,
        @Query("maxPrice") maxPrice: Int?,
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10
    ): Response<BaseResponse<ProductResponse>>

    @Headers("No-Authentication: true")
    @GET("products/{id}")
    suspend fun getProductById(
        @Path ("id") id: String
    ) : Response<BaseResponse<Product>>

}