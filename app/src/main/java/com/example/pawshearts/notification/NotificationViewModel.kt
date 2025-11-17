package com.example.pawshearts.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    private var currentUserId: String? = null

    fun loadNotifications(userId: String) {
        if (userId == currentUserId) return // Tránh load lại không cần thiết
        currentUserId = userId
        
        viewModelScope.launch {
            repository.getNotifications(userId).collect { notificationList ->
                _notifications.value = notificationList
            }
        }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            repository.markAsRead(id)
        }
    }

    fun deleteNotification(id: String) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun clearAll() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                repository.deleteAllForUser(userId)
            }
        }
    }
}