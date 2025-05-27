package com.blossy.flowerstore.presentation.notification.ui

import com.blossy.flowerstore.domain.model.NotificationModel

sealed class NotificationUiItem {
    data class Header(val label: String) : NotificationUiItem()
    data class Item(val notification: NotificationModel) : NotificationUiItem()
}