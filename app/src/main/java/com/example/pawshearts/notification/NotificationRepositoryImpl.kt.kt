package com.example.pawshearts.notification

import kotlinx.coroutines.flow.Flow

class NotificationRepositoryImpl(
    private val remoteDataSource: NotificationFirebaseSource,
    private val localDao: NotificationDao? = null
) : NotificationRepository {

    override fun getNotifications(userId: String): Flow<List<Notification>> {
        return remoteDataSource.getNotifications(userId)
    }

    override suspend fun deleteNotification(notificationId: String) {
        remoteDataSource.deleteNotification(notificationId)
        localDao?.deleteById(notificationId)
    }

    override suspend fun clearAllNotifications(userId: String) {
        remoteDataSource.clearAllNotifications(userId)
        localDao?.deleteAllForUser(userId)
    }
}
