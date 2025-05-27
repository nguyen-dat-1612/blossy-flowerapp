package com.blossy.flowerstore.domain.repository

import com.blossy.flowerstore.domain.model.PaymentModel
import com.blossy.flowerstore.domain.utils.Result

interface PaymentRepository {
    suspend fun createMomoPayment(orderId: String): Result<PaymentModel>

    suspend fun createVNPAYPayment(orderId: String): Result<PaymentModel>
}