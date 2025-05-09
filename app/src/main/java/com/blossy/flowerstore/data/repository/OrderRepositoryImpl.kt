package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.remote.api.OrderApi
import com.blossy.flowerstore.data.remote.dto.CreateOrderRequest
import com.blossy.flowerstore.data.remote.utils.OrderResponseWrapper
import com.blossy.flowerstore.domain.repository.OrderRepository
import com.blossy.flowerstore.domain.utils.Result
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderApi: OrderApi
) : OrderRepository{
    override suspend fun createOrder(orderRequest: CreateOrderRequest): Result<OrderResponseWrapper> {
        return try {
            val response = orderApi.createOrder(orderRequest)

            if (response.isSuccessful) {
                val orderResponse = response.body()
                if (orderResponse != null) {
                    Result.Success(orderResponse)
                } else {
                    Result.Error("Order response is null")
                }
            } else {
                Result.Error("Failed to create order: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("Error creating order: ${e.message}")

        }
    }
}