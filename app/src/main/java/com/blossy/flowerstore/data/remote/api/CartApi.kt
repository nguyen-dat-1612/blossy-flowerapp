package com.blossy.flowerstore.data.remote.api

import com.blossy.flowerstore.data.remote.dto.AddtoCartRequest
import com.blossy.flowerstore.data.remote.dto.CartResponse
import com.blossy.flowerstore.data.remote.utils.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CartApi {
    @POST("cart/add")
    suspend fun addToCart(@Body request: AddtoCartRequest): Response<BaseResponse<CartResponse>>

    @PUT("cart/update")
    suspend fun updateCart(@Body request: AddtoCartRequest): Response<BaseResponse<CartResponse>>

    @DELETE("cart/remove/{productId}")
    suspend fun removeFromCart(@Path ("productId") productId: String): Response<BaseResponse<CartResponse>>

    @GET("cart")
    suspend fun getCart(): Response<BaseResponse<CartResponse>>

    @GET("cart/clear")
    suspend fun clearCart(): Response<BaseResponse<Unit>>


}