package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.remote.api.PaymentApi
import com.blossy.flowerstore.data.remote.dto.CreatePaymentRequest
import com.blossy.flowerstore.data.remote.dto.PaymentResponse
import com.blossy.flowerstore.domain.repository.PaymentRepository
import com.blossy.flowerstore.domain.utils.Result
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val apiService: PaymentApi
) : PaymentRepository {
    override suspend fun createMomoPayment(orderId: String): Result<PaymentResponse> {
        return try {
            val response = apiService.createMomoPayment(CreatePaymentRequest(orderId))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.Success(body.data)
                } else {
                    Result.Error("Payment creation failed")
                }
            } else {
                Result.Error("Payment creation failed")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Payment creation failed")
        }
    }

    override suspend fun createVNPAYPayment(orderId: String): Result<PaymentResponse> {
        return try {
            val response = apiService.createVNPAYPayment(CreatePaymentRequest(orderId))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.Success(body.data)
                } else {
                    Result.Error("Payment creation failed")
                }
            } else {
                Result.Error("Payment creation failed")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Payment creation failed")
        }
    }
}