package com.blossy.flowerstore.domain.model.response

data class NextStepsModel(
    val action: String,
    val paymentEndpoint: String,
    val paymentData: PaymentDataModel
)