package com.blossy.flowerstore.domain.repository

import com.blossy.flowerstore.data.remote.dto.CreateOrderRequest
import com.blossy.flowerstore.data.remote.utils.OrderResponseWrapper
import com.blossy.flowerstore.domain.model.Order
import com.blossy.flowerstore.domain.utils.Result

interface OrderRepository {
    suspend fun createOrder(orderRequest: CreateOrderRequest): Result<OrderResponseWrapper>

    suspend fun myOrders(
        status: String,
        page: Int,
        limit: Int,
        isPaid: Boolean,
        sort: String
    ) : Result<List<Order>>

    suspend fun getOrderById(id: String): Result<Order>

    suspend fun cancelOrder(id: String, reason: String): Result<Order>

    suspend fun confirmOrder(id: String): Result<Order>


}