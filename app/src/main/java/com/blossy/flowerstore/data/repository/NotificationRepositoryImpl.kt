package com.blossy.flowerstore.data.repository

import androidx.lifecycle.ViewModel
import com.blossy.flowerstore.data.mapper.toNotification
import com.blossy.flowerstore.data.remote.api.NotificationApi
import com.blossy.flowerstore.domain.model.Notification
import com.blossy.flowerstore.domain.repository.NotificationRepository
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result

class NotificationRepositoryImpl @Inject constructor(
    private val notificationApi: NotificationApi
): NotificationRepository {
    override suspend fun getNotifications(): Result<List<Notification>> {
        return try {
            val response = notificationApi.getNotifications()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    val notifications = body.data?.map { it.toNotification() } ?: emptyList()
                    Result.Success(notifications)
                } else {
                    Result.Error(body?.message ?: "Unknown error occurred")
                }
            } else {
                Result.Error(response.message())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun deleteNotification(id: String): Result<Notification> {
        return try {
            val response = notificationApi.deleteNotification(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    val notification = body.data?.toNotification()
                    if (notification != null) {
                        Result.Success(notification)
                    } else {
                        Result.Error("Notification not found")
                    }
                } else {
                    Result.Error(body?.message ?: "Unknown error occurred")
                }
            } else {
                Result.Error(response.message())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun deleteAllNotifications(): Result<List<Notification>> {
        TODO("Not yet implemented")
    }


    override suspend fun readNotification(id: String): Result<Notification> {
        return try {
            val response = notificationApi.readNotification(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    val notification = body.data?.toNotification()
                    if (notification != null) {
                        Result.Success(notification)
                    } else {
                        Result.Error("Notification not found")
                    }
                } else {
                    Result.Error(body?.message ?: "Unknown error occurred")
                }
            } else {
                Result.Error(response.message())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }
}