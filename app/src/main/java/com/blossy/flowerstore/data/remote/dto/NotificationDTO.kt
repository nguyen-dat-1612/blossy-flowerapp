package com.blossy.flowerstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NotificationDTO(
    @SerializedName("_id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("isRead")
    val isRead: Boolean,
    @SerializedName("createdAt")
    val createdAt: String
)
