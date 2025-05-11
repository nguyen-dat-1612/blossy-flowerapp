package com.blossy.flowerstore.data.remote.api

import com.blossy.flowerstore.data.remote.dto.CancelOrderRequest
import com.blossy.flowerstore.data.remote.dto.CreateOrderRequest
import com.blossy.flowerstore.data.remote.dto.OrderResponse
import com.blossy.flowerstore.data.remote.utils.BaseResponse
import com.blossy.flowerstore.data.remote.utils.OrderResponseWrapper
import com.blossy.flowerstore.domain.model.Order
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderApi {

    @POST("orders")
    suspend fun createOrder(@Body orderRequest: CreateOrderRequest): Response<OrderResponseWrapper>

    @GET("orders/myorders")
    suspend fun myOrders(
        @Query("status") status: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("isPaid") isPaid: Boolean,
        @Query("sort") sort: String,
    ): Response<BaseResponse<OrderResponse>>


    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: String): Response<BaseResponse<Order>>

    @PUT("orders/{id}/cancel")
    suspend fun cancelOrder(@Path("id") id: String, @Body cancelOrderRequest: CancelOrderRequest): Response<BaseResponse<Order>>

    @PUT("orders/{id}/confirm")
    suspend fun confirmOrder(@Path("id") id: String): Response<BaseResponse<Order>>



}