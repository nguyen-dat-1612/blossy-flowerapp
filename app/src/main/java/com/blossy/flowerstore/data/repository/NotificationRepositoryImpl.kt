package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.mapper.toNotification
import com.blossy.flowerstore.data.remote.api.NotificationApi
import com.blossy.flowerstore.domain.model.NotificationModel
import com.blossy.flowerstore.domain.repository.NotificationRepository
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.data.remote.utils.safeApiCall
import com.blossy.flowerstore.data.remote.utils.toResult
import kotlinx.coroutines.withTimeout

class NotificationRepositoryImpl @Inject constructor(
    private val notificationApi: NotificationApi
): NotificationRepository {
    override suspend fun getNotifications(): Result<List<NotificationModel>> {
        return safeApiCall {
            notificationApi.getNotifications().toResult { response ->
                response.map { it.toNotification() }
            }
        }
    }

    override suspend fun deleteNotification(id: String): Result<NotificationModel> {
        return safeApiCall {
            notificationApi.deleteNotification(id).toResult { response ->
                response.toNotification()
            }
        }
    }

    override suspend fun deleteAllNotifications(): Result<List<NotificationModel>> {
        TODO("Not yet implemented")
    }


    override suspend fun readNotification(id: String): Result<NotificationModel>  {
        return safeApiCall {
            notificationApi.readNotification(id).toResult { response ->
                response.toNotification()
            }
        }
    }

}