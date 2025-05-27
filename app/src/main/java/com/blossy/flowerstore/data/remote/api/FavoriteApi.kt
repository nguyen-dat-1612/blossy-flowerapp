package com.blossy.flowerstore.data.remote.api

import com.blossy.flowerstore.data.remote.dto.FavoriteDTO
import com.blossy.flowerstore.data.remote.dto.ProductDTO
import com.blossy.flowerstore.data.remote.utils.BaseResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoriteApi {
    @GET("favorites")
    suspend fun getFavoriteProducts(): BaseResponse<List<ProductDTO>>

    @POST("favorites/{productId}")
    suspend fun addFavoriteProduct(@Path("productId") productId: String): BaseResponse<FavoriteDTO>

    @DELETE("favorites/{productId}")
    suspend fun removeFavoriteProduct(@Path("productId") productId: String): BaseResponse<Boolean>

    @GET("favorites/check/{productId}")
    suspend fun checkFavoriteProduct(@Path("productId") productId: String): BaseResponse<Boolean>

}