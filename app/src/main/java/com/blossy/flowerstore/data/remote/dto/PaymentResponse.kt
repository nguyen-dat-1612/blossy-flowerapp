package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PaymentResponse(
    val paymentId: String,
    @SerializedName("payUrl")
    val paymentUrl: String,
    val deeplink: String,
    val qrCodeUrl: String,
    val requestId: String,
    val orderId: String,
)