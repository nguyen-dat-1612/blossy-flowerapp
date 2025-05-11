package com.blossy.flowerstore.domain.usecase.order

import com.blossy.flowerstore.domain.repository.OrderRepository
import javax.inject.Inject

class MyOrdersUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(
        status: String,
        page: Int,
        limit: Int,
        isPaid: Boolean,
        sort: String
    ) = orderRepository.myOrders(status, page, limit, isPaid, sort)

}