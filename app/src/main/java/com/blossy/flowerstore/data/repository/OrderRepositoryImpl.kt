package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.remote.api.OrderApi
import com.blossy.flowerstore.data.remote.dto.CancelOrderRequest
import com.blossy.flowerstore.data.remote.dto.CreateOrderRequest
import com.blossy.flowerstore.data.remote.utils.OrderResponseWrapper
import com.blossy.flowerstore.domain.model.Order
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

    override suspend fun myOrders(
        status: String,
        page: Int,
        limit: Int,
        isPaid: Boolean,
        sort: String
    ): Result<List<Order>> {
        return try {
            val response = orderApi.myOrders(status, page, limit, isPaid, sort)
            if (response.isSuccessful) {
                val orderResponse = response.body()
                if (orderResponse != null && orderResponse.success) {
                    val orders = orderResponse.data?.orders ?: emptyList()
                    Result.Success(orders)
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

    override suspend fun getOrderById(id: String): Result<Order> {
        return try {
            val response = orderApi.getOrderById(id)
            if (response.isSuccessful) {
                val orderResponse = response.body()
                if (orderResponse != null && orderResponse.success) {
                    val order = orderResponse.data ?: throw Exception("Order data is null")
                    Result.Success(order)
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

    override suspend fun cancelOrder(id: String, reason: String): Result<Order> {
        return try {
            val response = orderApi.cancelOrder(id, CancelOrderRequest(reason))
            if (response.isSuccessful) {
                val orderResponse = response.body()
                if (orderResponse != null && orderResponse.success && orderResponse.data != null) {
                    Result.Success(orderResponse.data)
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

    override suspend fun confirmOrder(id: String): Result<Order> {
        return try {
            val response = orderApi.confirmOrder(id)

            if (response.isSuccessful) {
                val orderResponse = response.body()
                if (orderResponse != null && orderResponse.success && orderResponse.data != null) {
                    Result.Success(orderResponse.data)
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