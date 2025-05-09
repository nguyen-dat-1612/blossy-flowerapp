package com.blossy.flowerstore.domain.repository

import com.blossy.flowerstore.data.remote.dto.PaymentResponse
import com.blossy.flowerstore.domain.utils.Result

interface PaymentRepository {
    suspend fun createMomoPayment(orderId: String): Result<PaymentResponse>

    suspend fun createVNPAYPayment(orderId: String): Result<PaymentResponse>
}