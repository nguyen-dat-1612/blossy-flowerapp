package com.blossy.flowerstore.data.remote.api

import com.blossy.flowerstore.data.remote.dto.CreatePaymentDTO
import com.blossy.flowerstore.data.remote.dto.PaymentDTO
import com.blossy.flowerstore.data.remote.utils.BaseResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApi {
    @POST("payments/momo/create")
    suspend fun createMomoPayment(@Body request: CreatePaymentDTO): BaseResponse<PaymentDTO>

    @POST("payments/vnpay/create")
    suspend fun createVNPAYPayment(@Body request: CreatePaymentDTO): BaseResponse<PaymentDTO>

}