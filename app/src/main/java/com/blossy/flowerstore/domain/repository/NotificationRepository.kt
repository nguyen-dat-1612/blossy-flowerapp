package com.blossy.flowerstore.domain.repository

import com.blossy.flowerstore.domain.model.NotificationModel
import com.blossy.flowerstore.domain.utils.Result
interface NotificationRepository {

    suspend fun getNotifications(): Result<List<NotificationModel>>

    suspend fun deleteNotification(id: String): Result<NotificationModel>

    suspend fun deleteAllNotifications(): Result<List<NotificationModel>>

    suspend fun readNotification(id: String): Result<NotificationModel>

}