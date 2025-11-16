package com.example.pawshearts.notification

import kotlinx.coroutines.flow.Flow

class NotificationRepository(
    private val dao: NotificationDao,
    private val remote: NotificationFirebaseStore

) {
    // 1. Luồng chính: UI / ViewModel subscribe cái này
    fun getNotifications(userId: String): Flow<List<Notification>> {
        // Lấy từ Room, UI sẽ tự update khi Room thay đổi
        return dao.getNotificationsForUser(userId)
    }

    // 2. Đồng bộ từ Firebase về Room
    suspend fun syncFromRemote(userId: String) {
        // Gọi Firebase
        val remoteList = remote.fetchNotificationsForUser(userId)
        if (remoteList.isNotEmpty()) {
            // Lưu data xuống Room
            dao.upsertNotifications(remoteList)
        }
    }

    // 3. Đánh dấu đã đọc (local, optional: sync remote)
    suspend fun markAsRead(id: String) {
        dao.markAsRead(id)
        // TODO: nếu muốn sync lên Firebase:
        // remote.markAsReadRemote(id)
    }

    // 4. Xóa 1 thông báo
    suspend fun deleteById(id: String) {
        dao.deleteById(id)

        remote.deleteRemote(id)
    }

    // 5. Xóa tất cả thông báo của 1 user
    suspend fun deleteAllForUser(userId: String) {
        dao.deleteAllForUser(userId)

        remote.deleteAllForUserRemote(userId)
    }
}