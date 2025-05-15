package com.blossy.flowerstore.data.mapper

import com.blossy.flowerstore.data.remote.dto.NotificationResponse
import com.blossy.flowerstore.domain.model.Notification
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun NotificationResponse.toNotification(): Notification {
    return  Notification(
        userId = userId,
        type = type,
        title = title,
        message = message,
        read = isRead,
        createdAt = parseCreatedAt(createdAt)
    )
}
private fun parseCreatedAt(dateString: String): LocalDateTime {
    return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
}