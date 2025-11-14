package com.example.pawshearts.notifications

import com.example.pawshearts.data.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun listenNotifications(userId: String): Flow<List<Notification>>
    suspend fun sendNotification(notification: Notification)
    suspend fun markAsRead(id: String)
}
