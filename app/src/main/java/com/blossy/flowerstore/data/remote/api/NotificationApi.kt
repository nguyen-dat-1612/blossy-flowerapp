package com.blossy.flowerstore.data.remote.api

import com.blossy.flowerstore.data.remote.dto.NotificationResponse
import com.blossy.flowerstore.data.remote.utils.BaseResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotificationApi {
    @GET("notifications")
    suspend fun getNotifications(): Response<BaseResponse<List<NotificationResponse>>>

    @DELETE("notifications/{notificationId}")
    suspend fun deleteNotification(@Path("notificationId") id: String): Response<BaseResponse<NotificationResponse>>

    @DELETE("notifications")
    suspend fun deleteAllNotifications(): Response<BaseResponse<NotificationResponse>>

    @POST("/notifications/read/{notificationId}")
    suspend fun readNotification(@Path("notificationId") id: String): Response<BaseResponse<NotificationResponse>>
}