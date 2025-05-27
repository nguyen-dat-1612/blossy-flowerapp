package com.blossy.flowerstore.data.remote.api

import com.blossy.flowerstore.data.remote.dto.AddToCartDTO
import com.blossy.flowerstore.data.remote.dto.CartDTO
import com.blossy.flowerstore.data.remote.dto.UpdateCartDTO
import com.blossy.flowerstore.data.remote.utils.BaseResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CartApi {
    @POST("cart/add")
    suspend fun addToCart(@Body request: AddToCartDTO): BaseResponse<CartDTO>

    @PUT("cart/update")
    suspend fun updateCart(@Body request: UpdateCartDTO): BaseResponse<CartDTO>

    @DELETE("cart/remove/{productId}")
    suspend fun removeFromCart(@Path ("productId") productId: String): BaseResponse<CartDTO>

    @GET("cart")
    suspend fun getCart(): BaseResponse<CartDTO>

    @GET("cart/clear")
    suspend fun clearCart(): BaseResponse<Unit>
}