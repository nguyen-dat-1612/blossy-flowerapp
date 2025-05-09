package com.blossy.flowerstore.domain.usecase.payment

import com.blossy.flowerstore.data.remote.dto.PaymentResponse
import com.blossy.flowerstore.domain.repository.PaymentRepository
import com.blossy.flowerstore.domain.utils.Result
import javax.inject.Inject

class CreateMomoPaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(orderId: String): Result<PaymentResponse> {
        return paymentRepository.createMomoPayment(orderId)
    }
}