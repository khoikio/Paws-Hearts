package com.example.pawshearts.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    // user hiện tại (ai đang đăng nhập)
    private val _userId = MutableStateFlow<String?>(null)

    // UI sẽ lắng nghe biến này để vẽ list thông báo
    val notifications: StateFlow<List<Notification>> =
        _userId
            .filterNotNull()                         // chỉ chạy khi đã có userId
            .flatMapLatest { userId ->
                repository.getNotifications(userId)  // lấy từ Room qua DAO
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // gọi 1 lần khi mở màn hình thông báo (truyền userId vô)
    fun loadNotifications(userId: String) {
        _userId.value = userId

        // sync từ Firebase về Room
        viewModelScope.launch {
            repository.syncFromRemote(userId)
        }
    }

    // đánh dấu đã đọc 1 thông báo
    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            repository.markAsRead(notificationId)
        }
    }

    // xóa 1 thông báo
    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            repository.deleteById(notificationId)
        }
    }

    // xóa tất cả thông báo của user hiện tại
    fun clearAll() {
        val uid = _userId.value ?: return
        viewModelScope.launch {
            repository.deleteAllForUser(uid)
        }
    }

    // nếu muốn kéo để refresh
    fun refresh() {
        val uid = _userId.value ?: return
        viewModelScope.launch {
            repository.syncFromRemote(uid)
        }
    }
}