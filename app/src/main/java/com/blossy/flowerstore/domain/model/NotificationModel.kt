package com.blossy.flowerstore.domain.model

import java.time.LocalDateTime

data class NotificationModel (
    val userId: String,
    val type: String,
    val title: String,
    val message: String,
    val read: Boolean,
    val createdAt: LocalDateTime
)