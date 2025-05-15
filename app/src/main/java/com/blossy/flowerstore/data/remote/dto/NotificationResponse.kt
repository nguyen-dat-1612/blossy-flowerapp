package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    @SerializedName("_id")
    val id: String,
    val userId: String,
    val type: String,
    val title: String,
    val message: String,
    val isRead: Boolean,
    val createdAt: String
)
