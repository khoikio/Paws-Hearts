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
        //ID rông thì firestore tự tạo
        val docRef = if (notification.id.isBlank()) {
            db.collection("notifications").document()
        } else {
            db.collection("notifications").document(notification.id)
        }

        // Cần cập nhật ID nếu nó được tạo tự động
        val finalNotification = if (notification.id.isBlank()) {
            notification.copy(id = docRef.id)
        } else notification

        docRef.set(finalNotification).await()
    }

    override suspend fun markAsRead(id: String) {
        db.collection("notifications")
            .document(id)
            .update("isRead", true).await()// thêm await là hàm suspend đúng nghĩa,suspend là hàm bất đồng bộ
    }

    // ------------------- Các hàm chuyên dụng -------------------

    override suspend fun notifyNewPost(post: Post) {
        // Dùng postId chứ không dùng post.id
        // Lấy tất cả người dùng trừ author (Giả định là GỬI CHO TẤT CẢ TẠM THỜI)
        val users = db.collection("users").get().await().documents
            .mapNotNull { it.id }
            .filter { it != post.userId }

        users.forEach { uid ->
            val notification = Notification(
                id = "", // Để rỗng, hàm sendNotification sẽ tự tạo ID
                receiverId = uid,
                senderId = post.userId,
                postId = post.postId, // Dùng postId
                type = Notification.Type.NEW_POST,
                message = "${post.username ?: "Một người dùng"} đã đăng bài mới!",
                timestamp = System.currentTimeMillis(),
                isRead = false
            )
            sendNotification(notification)
        }
    }

    override suspend fun notifyLike(post: Post, likerId: String) {
        if (likerId == post.userId) return

        val notification = Notification(
            id = "",
            receiverId = post.userId,
            senderId = likerId,
            postId = post.postId, // Dùng postId
            type = Notification.Type.LIKE,
            message = "${post.username ?: "Một người dùng"} thích bài viết của bạn.",
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        sendNotification(notification)
    }

    override suspend fun notifyComment(post: Post, commenterId: String) {
        if (commenterId == post.userId) return

        val notification = Notification(
            id = "",
            receiverId = post.userId,
            senderId = commenterId,
            postId = post.postId, // Dùng postId
            type = Notification.Type.COMMENT,
            message = "${post.username ?: "Một người dùng"} bình luận bài viết của bạn.",
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        sendNotification(notification)
    }
}