package com.blossy.flowerstore.domain.repository

import com.blossy.flowerstore.data.remote.dto.CreateOrderRequest
import com.blossy.flowerstore.data.remote.utils.OrderResponseWrapper
import com.blossy.flowerstore.domain.utils.Result

interface OrderRepository {
    suspend fun createOrder(orderRequest: CreateOrderRequest): Result<OrderResponseWrapper>
}