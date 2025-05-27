package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NextStepsDTO(
    @SerializedName("action")
    val action: String,
    @SerializedName("paymentEndpoint")
    val paymentEndpoint: String,
    @SerializedName("paymentData")
    val paymentData: PaymentDataDTO
)
