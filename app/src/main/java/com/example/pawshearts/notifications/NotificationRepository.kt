package com.example.pawshearts.notifications

import com.example.pawshearts.data.model.Notification
import kotlinx.coroutines.flow.Flow
import com.example.pawshearts.post.Post

interface NotificationRepository {
    fun listenNotifications(userId: String): Flow<List<Notification>>
    suspend fun sendNotification(notification: Notification)
    suspend fun markAsRead(id: String)

    // Gửi thông báo khi có bài post mới (cần Post object để lấy đủ thông tin)
    suspend fun notifyNewPost(post: Post)

    // Gửi thông báo khi có người like/tym (cần Post object và ID người like)
    suspend fun notifyLike(post: Post, likerId: String)

    // Gửi thông báo khi có người comment (cần Post object và ID người comment)
    suspend fun notifyComment(post: Post, commenterId: String)
}
//suspend fun là hàm tạm ngưng, chạy bất đồng bộ, tạm dừng nhưng không chặn thread hiện tại để có thông báo thì mấy cái kia chạy bình thường á
