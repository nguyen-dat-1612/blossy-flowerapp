package com.blossy.flowerstore.domain.repository

import com.blossy.flowerstore.domain.model.Notification
import com.blossy.flowerstore.domain.utils.Result
interface NotificationRepository {

    suspend fun getNotifications(): Result<List<Notification>>

    suspend fun deleteNotification(id: String): Result<Notification>

    suspend fun deleteAllNotifications(): Result<List<Notification>>

    suspend fun readNotification(id: String): Result<Notification>

}