package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AddressResponse (
    @SerializedName("_id")
    val id: String?,
    val name: String,
    val phone: String,
    val address: String,
    val isDefault: Boolean
)