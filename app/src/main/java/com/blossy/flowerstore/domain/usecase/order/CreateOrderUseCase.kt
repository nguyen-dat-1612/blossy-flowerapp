package com.blossy.flowerstore.domain.usecase.order

import com.blossy.flowerstore.data.remote.dto.CreateOrderRequest
import com.blossy.flowerstore.domain.repository.OrderRepository
import javax.inject.Inject

class CreateOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
){
    suspend operator fun invoke(orderRequest: CreateOrderRequest) = orderRepository.createOrder(orderRequest)

}