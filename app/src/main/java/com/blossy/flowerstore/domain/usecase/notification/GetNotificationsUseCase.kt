package com.blossy.flowerstore.domain.usecase.notification

import com.blossy.flowerstore.domain.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke() = notificationRepository.getNotifications()
}