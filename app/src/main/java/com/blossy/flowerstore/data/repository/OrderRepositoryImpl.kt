package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.mapper.toOrder
import com.blossy.flowerstore.data.mapper.toRequest
import com.blossy.flowerstore.data.remote.api.OrderApi
import com.blossy.flowerstore.data.remote.dto.CancelOrderDTO
import com.blossy.flowerstore.data.remote.dto.CreateOrderDTO
import com.blossy.flowerstore.data.remote.utils.OrderResponseWrapper
import com.blossy.flowerstore.domain.model.OrderModel
import com.blossy.flowerstore.domain.repository.OrderRepository
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.data.remote.utils.safeApiCall
import com.blossy.flowerstore.data.remote.utils.toResult
import com.blossy.flowerstore.data.remote.utils.toWrappedResult
import com.blossy.flowerstore.domain.model.request.CreateOrderModel
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderApi: OrderApi
) : OrderRepository{

    override suspend fun createOrder(orderRequest: CreateOrderModel): Result<OrderResponseWrapper> {
        return safeApiCall {
                val request = orderRequest.toRequest()
                orderApi.createOrder(request).toWrappedResult { response ->
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
    ): Result<List<OrderModel>> {
        return safeApiCall {
            orderApi.myOrders(status, page, limit, isPaid, sort).toResult {
                it.orders.map {
                    it.toOrder()
                }
            }
        }
    }

    override suspend fun getOrderById(id: String): Result<OrderModel> {
        return safeApiCall {
            orderApi.getOrderById(id).toResult {
                it.toOrder()
            }
        }
    }

    override suspend fun cancelOrder(id: String, reason: String): Result<OrderModel>  {
        return safeApiCall {
            orderApi.cancelOrder(id, CancelOrderDTO(reason))
                .toResult { it.toOrder() }
        }
    }

    override suspend fun confirmOrder(id: String): Result<OrderModel> {
        return safeApiCall {
            orderApi.confirmOrder(id).toResult {
                it.toOrder()
            }
        }
    }
}