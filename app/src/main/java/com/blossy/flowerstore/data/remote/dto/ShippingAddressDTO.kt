package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ShippingAddressDTO (
    @SerializedName("recipientName") val name: String,
    @SerializedName("phoneNumber") val phone: String,
    @SerializedName("address") val address: String
)