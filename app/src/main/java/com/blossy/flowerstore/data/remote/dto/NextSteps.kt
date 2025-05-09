package com.blossy.flowerstore.data.remote.dto

data class NextSteps(
    val action: String,
    val paymentEndpoint: String,
    val paymentData: PaymentData
)

data class PaymentData(
    val orderId: String
)