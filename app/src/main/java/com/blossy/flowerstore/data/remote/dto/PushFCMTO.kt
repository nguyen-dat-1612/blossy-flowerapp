package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PushFCMTO(
    @SerializedName("fcm")
    val fcm: String
)
