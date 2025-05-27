package com.blossy.flowerstore.domain.usecase.payment

import com.blossy.flowerstore.domain.repository.PaymentRepository
import javax.inject.Inject

class CreateMomoPaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(orderId: String) = paymentRepository.createMomoPayment(orderId)
}