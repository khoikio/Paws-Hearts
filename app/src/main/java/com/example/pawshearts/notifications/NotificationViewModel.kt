package com.example.pawshearts.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawshearts.data.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repo: NotificationRepository,
    private val currentUserId: String
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    init {
        listenNotifications()
    }

    private fun listenNotifications() {
        viewModelScope.launch {
            repo.listenNotifications(currentUserId).collect {
                _notifications.value = it.reversed() // mới nhất lên đầu
            }
        }
    }

    fun sendNotification(notification: Notification) {
        viewModelScope.launch {
            repo.sendNotification(notification)
        }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            repo.markAsRead(id)
        }
    }
}
