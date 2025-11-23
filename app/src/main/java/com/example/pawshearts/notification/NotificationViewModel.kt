package com.example.pawshearts.notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NotificationViewModel(
    app: Application,
    private val repository: NotificationRepository
) : AndroidViewModel(app) {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadNotifications(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getNotifications(userId)
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { list ->
                    _notifications.value = list
                    _isLoading.value = false
                }
        }
    }

    fun deleteNotification(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteNotification(id)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearAll(userId: String) {
        viewModelScope.launch {
            try {
                repository.clearAllNotifications(userId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
