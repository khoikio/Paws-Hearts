package com.example.pawshearts.notification

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    // Lấy tất cả thông báo của 1 user, mới nhất ở trên
    @Query("SELECT * FROM notification WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotificationsForUser(userId: String): Flow<List<Notification>>

    // Lưu / cập nhật danh sách thông báo
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertNotifications(notifications: List<Notification>)

    // Đánh dấu 1 thông báo đã đọc
    @Query("UPDATE notification SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    // Xóa 1 thông báo
    @Query("DELETE FROM notification WHERE id = :id")
    suspend fun deleteById(id: String)

    // Xóa tất cả thông báo của 1 user
    @Query("DELETE FROM notification WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)
}
