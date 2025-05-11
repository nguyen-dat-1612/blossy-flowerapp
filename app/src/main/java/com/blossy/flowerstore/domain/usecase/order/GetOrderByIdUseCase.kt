package com.blossy.flowerstore.domain.usecase.order

import com.blossy.flowerstore.domain.repository.OrderRepository
import javax.inject.Inject

class GetOrderByIdUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(id: String) = orderRepository.getOrderById(id)
}