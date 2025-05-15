package com.blossy.flowerstore.presentation.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.model.Notification
import com.blossy.flowerstore.domain.usecase.notification.GetNotificationsUseCase
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.notification.ui.NotificationUiItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase
) : ViewModel() {

    private val _notifications = MutableStateFlow<UiState<List<NotificationUiItem>>>(UiState.Idle)
    val notifications: StateFlow<UiState<List<NotificationUiItem>>> = _notifications

    fun fetchNotifications() {
        viewModelScope.launch {
            _notifications.value = UiState.Loading
            when (val result = getNotificationsUseCase()) {
                is Result.Success -> {
                    val grouped = groupNotifications(result.data)
                    _notifications.value = UiState.Success(grouped)
                }

                is Result.Error -> {
                    _notifications.value = UiState.Error(result.message)
                }
                else -> {
                }
            }
        }
    }

    private fun groupNotifications(notifications: List<Notification>): List<NotificationUiItem> {
        val now = LocalDate.now()
        val grouped = linkedMapOf<String, MutableList<Notification>>()

        for (notification in notifications) {
            val date = notification.createdAt.toLocalDate()

            val label = when {
                date == now -> "Today"
                date == now.minusDays(1) -> "Yesterday"
                date >= now.minusDays(7) -> "This Week"
                else -> date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            }

            grouped.getOrPut(label) { mutableListOf() }.add(notification)
        }

        val result = mutableListOf<NotificationUiItem>()
        for ((label, list) in grouped) {
            result.add(NotificationUiItem.Header(label))
            result.addAll(list.map { NotificationUiItem.Item(it) })
        }

        return result
    }
}
