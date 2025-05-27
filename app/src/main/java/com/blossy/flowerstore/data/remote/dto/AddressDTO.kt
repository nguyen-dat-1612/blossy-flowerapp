package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AddressDTO (
    @SerializedName("_id")
    val id: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("isDefault")
    val isDefault: Boolean
)