package com.example.pawshearts.notification

import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(userId: String): Flow<List<Notification>>
    suspend fun deleteNotification(notificationId: String)
    // THÊM HÀM MỚI
    suspend fun clearAllNotifications(userId: String)
}
