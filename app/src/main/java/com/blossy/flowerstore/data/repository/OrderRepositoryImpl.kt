package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.remote.api.OrderApi
import com.blossy.flowerstore.data.remote.dto.CancelOrderRequest
import com.blossy.flowerstore.data.remote.dto.CreateOrderRequest
import com.blossy.flowerstore.data.remote.utils.OrderResponseWrapper
import com.blossy.flowerstore.domain.model.Order
import com.blossy.flowerstore.domain.repository.OrderRepository
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.data.remote.utils.safeApiCall
import com.blossy.flowerstore.data.remote.utils.toResult
import com.blossy.flowerstore.data.remote.utils.toWrappedResult
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderApi: OrderApi
) : OrderRepository{

    override suspend fun createOrder(orderRequest: CreateOrderRequest): Result<OrderResponseWrapper> =
        withTimeout(TIMEOUT) {
            safeApiCall {
                orderApi.createOrder(orderRequest).toWrappedResult { response ->
                    response
                }
            }
        }

    override suspend fun myOrders(
        status: String,
        page: Int,
        limit: Int,
        isPaid: Boolean,
        sort: String
    ): Result<List<Order>> = withTimeout(TIMEOUT){
        safeApiCall {
            orderApi.myOrders(status, page, limit, isPaid, sort).toResult { response ->
                response.orders
            }
        }
    }

    override suspend fun getOrderById(id: String): Result<Order> = withTimeout(TIMEOUT) {
        safeApiCall {
            orderApi.getOrderById(id).toResult()
        }
    }

    override suspend fun cancelOrder(id: String, reason: String): Result<Order> = withTimeout(TIMEOUT) {
        safeApiCall {
            orderApi.cancelOrder(id, CancelOrderRequest(reason)).toResult()
        }
    }

    override suspend fun confirmOrder(id: String): Result<Order> = withTimeout(TIMEOUT){
        safeApiCall {
            orderApi.confirmOrder(id).toResult()
        }
    }

    companion object {
        private const val TIMEOUT = 5000L
    }
}