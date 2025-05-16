package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.mapper.toNotification
import com.blossy.flowerstore.data.remote.api.NotificationApi
import com.blossy.flowerstore.domain.model.Notification
import com.blossy.flowerstore.domain.repository.NotificationRepository
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.data.remote.utils.safeApiCall
import com.blossy.flowerstore.data.remote.utils.toResult
import kotlinx.coroutines.withTimeout

class NotificationRepositoryImpl @Inject constructor(
    private val notificationApi: NotificationApi
): NotificationRepository {
    override suspend fun getNotifications(): Result<List<Notification>> = withTimeout(TIMEOUT){
        safeApiCall {
            notificationApi.getNotifications().toResult { response ->
                response.map { it.toNotification() }
            }
        }
    }

    override suspend fun deleteNotification(id: String): Result<Notification> = withTimeout(TIMEOUT){
        safeApiCall {
            notificationApi.deleteNotification(id).toResult { response ->
                response.toNotification()
            }
        }
    }

    override suspend fun deleteAllNotifications(): Result<List<Notification>> {
        TODO("Not yet implemented")
    }


    override suspend fun readNotification(id: String): Result<Notification> = withTimeout(TIMEOUT) {
        safeApiCall {
            notificationApi.readNotification(id).toResult { response ->
                response.toNotification()
            }
        }
    }

    companion object {
        private const val TIMEOUT = 5000L
    }
}