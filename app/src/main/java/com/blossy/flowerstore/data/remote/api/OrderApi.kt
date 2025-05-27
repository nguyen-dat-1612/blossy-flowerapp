package com.blossy.flowerstore.data.remote.api

import com.blossy.flowerstore.data.remote.dto.CancelOrderDTO
import com.blossy.flowerstore.data.remote.dto.CreateOrderDTO
import com.blossy.flowerstore.data.remote.dto.OrderDTO
import com.blossy.flowerstore.data.remote.dto.OrderListDTO
import com.blossy.flowerstore.data.remote.utils.BaseResponse
import com.blossy.flowerstore.data.remote.utils.OrderResponseWrapper
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderApi {

    @POST("orders")
    suspend fun createOrder(@Body orderRequest: CreateOrderDTO): Response<OrderResponseWrapper>

    @GET("orders/myorders")
    suspend fun myOrders(
        @Query("status") status: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("isPaid") isPaid: Boolean,
        @Query("sort") sort: String,
    ): BaseResponse<OrderListDTO>


    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: String): BaseResponse<OrderDTO>

    @PUT("orders/{id}/cancel")
    suspend fun cancelOrder(@Path("id") id: String, @Body cancelOrderRequest: CancelOrderDTO): BaseResponse<OrderDTO>

    @PUT("orders/{id}/confirm")
    suspend fun confirmOrder(@Path("id") id: String): BaseResponse<OrderDTO>
}