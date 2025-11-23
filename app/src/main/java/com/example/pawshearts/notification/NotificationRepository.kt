package com.example.pawshearts.notification

import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(userId: String): Flow<List<Notification>>
    suspend fun deleteNotification(notificationId: String)
    suspend fun clearAllNotifications(userId: String)
}
