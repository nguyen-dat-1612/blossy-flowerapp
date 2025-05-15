package com.blossy.flowerstore.presentation.notification.ui

import com.blossy.flowerstore.domain.model.Notification

sealed class NotificationUiItem {
    data class Header(val label: String) : NotificationUiItem()
    data class Item(val notification: Notification) : NotificationUiItem()
}