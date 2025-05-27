package com.blossy.flowerstore.domain.repository

import com.blossy.flowerstore.data.remote.dto.CreateOrderDTO
import com.blossy.flowerstore.data.remote.utils.OrderResponseWrapper
import com.blossy.flowerstore.domain.model.OrderModel
import com.blossy.flowerstore.domain.model.request.CreateOrderModel
import com.blossy.flowerstore.domain.utils.Result

interface OrderRepository {
    suspend fun createOrder(orderRequest: CreateOrderModel): Result<OrderResponseWrapper>

    suspend fun myOrders(
        status: String,
        page: Int,
        limit: Int,
        isPaid: Boolean,
        sort: String
    ) : Result<List<OrderModel>>

    suspend fun getOrderById(id: String): Result<OrderModel>

    suspend fun cancelOrder(id: String, reason: String): Result<OrderModel>

    suspend fun confirmOrder(id: String): Result<OrderModel>


}