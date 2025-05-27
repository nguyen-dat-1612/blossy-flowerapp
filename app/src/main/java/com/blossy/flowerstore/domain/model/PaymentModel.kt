package com.blossy.flowerstore.domain.model

import com.google.gson.annotations.SerializedName

data class PaymentModel(
    val paymentId: String,
    val paymentUrl: String,
    val deeplink: String,
    val qrCodeUrl: String,
    val requestId: String,
    val orderId: String,
)