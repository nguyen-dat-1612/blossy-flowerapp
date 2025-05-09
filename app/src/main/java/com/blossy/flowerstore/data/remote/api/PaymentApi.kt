package com.blossy.flowerstore.data.remote.api

import com.blossy.flowerstore.data.remote.dto.CreatePaymentRequest
import com.blossy.flowerstore.data.remote.dto.PaymentResponse
import com.blossy.flowerstore.data.remote.utils.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApi {
    @POST("payments/momo/create")
    suspend fun createMomoPayment(@Body request: CreatePaymentRequest): Response<BaseResponse<PaymentResponse>>

    @POST("payments/vnpay/create")
    suspend fun createVNPAYPayment(@Body request: CreatePaymentRequest): Response<BaseResponse<PaymentResponse>>

}