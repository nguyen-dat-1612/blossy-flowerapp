package com.blossy.flowerstore.data.remote.api

import com.blossy.flowerstore.data.remote.dto.CreateOrderRequest
import com.blossy.flowerstore.data.remote.utils.BaseResponse
import com.blossy.flowerstore.data.remote.utils.OrderResponseWrapper
import com.blossy.flowerstore.domain.model.Order
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OrderApi {

    @POST("orders")
    suspend fun createOrder(@Body orderRequest: CreateOrderRequest): Response<OrderResponseWrapper>

}