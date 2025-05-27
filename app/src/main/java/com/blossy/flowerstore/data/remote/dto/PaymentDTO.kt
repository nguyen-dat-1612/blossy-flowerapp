package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PaymentDTO(
    @SerializedName("paymentId")
    val paymentId: String,
    @SerializedName("payUrl")
    val paymentUrl: String,
    @SerializedName("deeplink")
    val deeplink: String,
    @SerializedName("qrCodeUrl")
    val qrCodeUrl: String,
    @SerializedName("requestId")
    val requestId: String,
    @SerializedName("orderId")
    val orderId: String,
)