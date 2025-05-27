package com.blossy.flowerstore.data.remote.api

import com.blossy.flowerstore.data.remote.dto.NotificationDTO
import com.blossy.flowerstore.data.remote.utils.BaseResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationApi {
    @GET("notifications")
    suspend fun getNotifications(): BaseResponse<List<NotificationDTO>>

    @DELETE("notifications/{notificationId}")
    suspend fun deleteNotification(@Path("notificationId") id: String): BaseResponse<NotificationDTO>

    @DELETE("notifications")
    suspend fun deleteAllNotifications(): BaseResponse<NotificationDTO>

    @POST("/notifications/read/{notificationId}")
    suspend fun readNotification(@Path("notificationId") id: String): BaseResponse<NotificationDTO>
}