package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.remote.api.PaymentApi
import com.blossy.flowerstore.data.remote.dto.CreatePaymentRequest
import com.blossy.flowerstore.data.remote.dto.PaymentResponse
import com.blossy.flowerstore.domain.repository.PaymentRepository
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.data.remote.utils.safeApiCall
import com.blossy.flowerstore.data.remote.utils.toResult
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val apiService: PaymentApi
) : PaymentRepository {
    override suspend fun createMomoPayment(orderId: String): Result<PaymentResponse> = withTimeout(TIMEOUT){
        safeApiCall {
            apiService.createMomoPayment(CreatePaymentRequest(orderId)).toResult()
        }
    }

    override suspend fun createVNPAYPayment(orderId: String): Result<PaymentResponse> = withTimeout(TIMEOUT) {
        safeApiCall {
            apiService.createVNPAYPayment(CreatePaymentRequest(orderId)).toResult()
        }
    }

    companion object {
        private const val TIMEOUT = 5000L
    }
}