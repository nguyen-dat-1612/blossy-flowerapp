package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreatePaymentDTO(
    @SerializedName("orderId")
    val orderId: String
)