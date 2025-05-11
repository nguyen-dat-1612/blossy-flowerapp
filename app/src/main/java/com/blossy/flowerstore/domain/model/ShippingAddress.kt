package com.blossy.flowerstore.domain.model

import com.google.gson.annotations.SerializedName

data class ShippingAddress (
    @SerializedName("recipientName") val name: String,
    @SerializedName("phoneNumber") val phone: String,
    @SerializedName("address") val address: String,
    @SerializedName("city") val city: String,
    @SerializedName("postalCode") val postalCode: String,
    @SerializedName("country") val country: String
)