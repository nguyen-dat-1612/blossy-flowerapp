package com.blossy.flowerstore.domain.usecase.order

import com.blossy.flowerstore.domain.repository.OrderRepository
import javax.inject.Inject

class CancelOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(id: String, reason: String) = orderRepository.cancelOrder(id, reason)

}