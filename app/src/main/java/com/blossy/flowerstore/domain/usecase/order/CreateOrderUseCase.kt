package com.blossy.flowerstore.domain.usecase.order

import com.blossy.flowerstore.data.remote.dto.CreateOrderDTO
import com.blossy.flowerstore.domain.model.request.CreateOrderModel
import com.blossy.flowerstore.domain.repository.OrderRepository
import javax.inject.Inject

class CreateOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
){
    suspend operator fun invoke(orderRequest: CreateOrderModel) = orderRepository.createOrder(orderRequest)

}