package com.example.pawshearts.notification

import kotlinx.coroutines.flow.Flow

class NotificationRepository(
    private val remote: NotificationFirebaseStore
) {
    // Luồng chính: UI / ViewModel subscribe cái này để lấy dữ liệu trực tiếp từ Firebase
    fun getNotifications(userId: String): Flow<List<Notification>> {
        return remote.getNotificationsFlowForUser(userId)
    }

    // Đánh dấu đã đọc (chỉ cần gọi lên Firebase)
    suspend fun markAsRead(id: String) {
        remote.markAsReadRemote(id)
    }

    // Xóa 1 thông báo
    suspend fun deleteById(id: String) {
        remote.deleteRemote(id)
    }

    // Xóa tất cả thông báo của 1 user
    suspend fun deleteAllForUser(userId: String) {
        remote.deleteAllForUserRemote(userId)
    }
}