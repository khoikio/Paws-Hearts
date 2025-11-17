package com.example.pawshearts.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    fun getNotifications(userId: String): StateFlow<List<Notification>> {
        return repository.getNotifications(userId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            repository.deleteNotification(notificationId)
        }
    }

    // THÊM HÀM MỚI
    fun clearAllNotifications(userId: String) {
        viewModelScope.launch {
            repository.clearAllNotifications(userId)
        }
    }
}
