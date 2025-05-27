package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.mapper.toPayment
import com.blossy.flowerstore.data.remote.api.PaymentApi
import com.blossy.flowerstore.data.remote.dto.CreatePaymentDTO
import com.blossy.flowerstore.domain.repository.PaymentRepository
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.data.remote.utils.safeApiCall
import com.blossy.flowerstore.data.remote.utils.toResult
import com.blossy.flowerstore.domain.model.PaymentModel
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val apiService: PaymentApi
) : PaymentRepository {
    override suspend fun createMomoPayment(orderId: String): Result<PaymentModel> {
        return safeApiCall {
            apiService.createMomoPayment(CreatePaymentDTO(orderId)).toResult() {
                it.toPayment()
            }
        }
    }

    override suspend fun createVNPAYPayment(orderId: String): Result<PaymentModel>  {
        return safeApiCall {
            apiService.createVNPAYPayment(CreatePaymentDTO(orderId)).toResult() {
                it.toPayment()
            }
        }
    }

}