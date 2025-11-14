package com.example.pawshearts.notifications

import com.example.pawshearts.data.model.Notification
import com.example.pawshearts.post.Post
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import java.util.*

class NotificationRepositoryImpl(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : NotificationRepository {

    override fun listenNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val listener = db.collection("notifications")
            .whereEqualTo("receiverId", userId)
            .orderBy("timestamp")
            .addSnapshotListener { snap, e ->
                if (e != null || snap == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snap.documents.mapNotNull { it.toObject(Notification::class.java) }
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun sendNotification(notification: Notification) {
        db.collection("notifications")
            .document(notification.id)
            .set(notification)
    }

    override suspend fun markAsRead(id: String) {
        db.collection("notifications")
            .document(id)
            .update("isRead", true)
    }

    // ------------------- Các hàm chuyên dụng -------------------

    suspend fun notifyNewPost(post: Post) {
        // Lấy tất cả người dùng trừ author
        val users = db.collection("users").get().await().documents
            .mapNotNull { it.id }
            .filter { it != post.userId }

        users.forEach { uid ->
            val notification = Notification(
                id = UUID.randomUUID().toString(),
                receiverId = uid,
                senderId = post.userId,
                postId = post.id,
                type = Notification.Type.NEW_POST,
                message = "${post.username ?: "Một người dùng"} đã đăng bài mới",
                timestamp = System.currentTimeMillis(),
                isRead = false
            )
            sendNotification(notification)
        }
    }

    suspend fun notifyLike(post: Post, likerId: String) {
        // Không gửi nếu like chính chủ
        if (likerId == post.userId) return

        val notification = Notification(
            id = UUID.randomUUID().toString(),
            receiverId = post.userId,
            senderId = likerId,
            id = post.id,
            type = Notification.Type.LIKE,
            message = "Có người thích bài viết của bạn",
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        sendNotification(notification)
    }

    suspend fun notifyComment(post: Post, commenterId: String) {
        if (commenterId == post.userId) return

        val notification = Notification(
            id = UUID.randomUUID().toString(),
            receiverId = post.userId,
            senderId = commenterId,
            postId = post.id,
            type = Notification.Type.COMMENT,
            message = "Có người bình luận bài viết của bạn",
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        sendNotification(notification)
    }
}
