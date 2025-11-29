package com.example.pawshearts.notification

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notification WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotificationsForUser(userId: String): Flow<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertNotifications(notifications: List<Notification>)

    @Query("UPDATE notification SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("DELETE FROM notification WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM notification WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)
}
