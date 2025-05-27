package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PaymentDataDTO(
    @SerializedName("orderId")
    val orderId: String
)