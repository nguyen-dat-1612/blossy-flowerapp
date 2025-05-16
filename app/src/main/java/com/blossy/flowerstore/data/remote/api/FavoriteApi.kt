package com.blossy.flowerstore.data.remote.api

import com.blossy.flowerstore.data.remote.dto.FavoriteResponse
import com.blossy.flowerstore.data.remote.utils.BaseResponse
import com.blossy.flowerstore.domain.model.Product
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoriteApi {

    @GET("favorites")
    suspend fun getFavoriteProducts(): Response<BaseResponse<List<Product>>>

    @POST("favorites/{productId}")
    suspend fun addFavoriteProduct(@Path("productId") productId: String): Response<BaseResponse<FavoriteResponse>>

    @DELETE("favorites/{productId}")
    suspend fun removeFavoriteProduct(@Path("productId") productId: String): Response<BaseResponse<Boolean>>

    @GET("favorites/check/{productId}")
    suspend fun checkFavoriteProduct(@Path("productId") productId: String): Response<BaseResponse<Boolean>>

}