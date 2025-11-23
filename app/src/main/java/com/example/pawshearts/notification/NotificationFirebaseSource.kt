package com.example.pawshearts.notification

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NotificationFirebaseSource(private val firestore: FirebaseFirestore) {

    fun getNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val listener = firestore.collection("pending_notifications")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", " Lỗi truy vấn Firestore", error)
                    close(error)
                    return@addSnapshotListener
                }

                val notifications = snapshot?.toObjects(Notification::class.java) ?: emptyList()
                trySend(notifications)
            }

        awaitClose { listener.remove() }
    }



    suspend fun deleteNotification(notificationId: String) {
        firestore.collection("pending_notifications").document(notificationId).delete().await()
    }

    suspend fun clearAllNotifications(userId: String) {
        val querySnapshot = firestore.collection("pending_notifications")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        val batch = firestore.batch()
        for (doc in querySnapshot.documents) batch.delete(doc.reference)
        batch.commit().await()
    }

    private suspend fun sendNotification(data: Map<String, Any>) {
        val doc = firestore.collection("pending_notifications").document()
        val finalData = data + mapOf("id" to doc.id)
        doc.set(finalData).await()
    }

    // 🔔 Gửi thông báo khi tạo bài viết
    suspend fun sendPostNotification(
        receiverId: String,
        actorId: String,
        actorName: String?,
        actorAvatarUrl: String?,
        postId: String
    ) {
        sendNotification(
            mapOf(
                "userId" to receiverId,
                "actorId" to actorId,
                "actorName" to (actorName ?: "Người dùng"),
                "actorAvatarUrl" to (actorAvatarUrl ?: ""),
                "postId" to postId,
                "type" to "post",
                "message" to "đã đăng một bài viết mới 🐾",
                "createdAt" to Timestamp.now()
            )
        )
    }

    // ❤️ Gửi thông báo khi ai đó thả tim
    suspend fun sendLikeNotification(
        receiverId: String,
        actorId: String,
        actorName: String?,
        actorAvatarUrl: String?,
        postId: String
    ) {
        sendNotification(
            mapOf(
                "userId" to receiverId,
                "actorId" to actorId,
                "actorName" to (actorName ?: "Người dùng"),
                "actorAvatarUrl" to (actorAvatarUrl ?: ""),
                "postId" to postId,
                "type" to "like",
                "message" to "đã thích bài viết của bạn ❤️",
                "createdAt" to Timestamp.now()
            )
        )
    }

    // 💬 Gửi thông báo khi có bình luận
    suspend fun sendCommentNotification(
        receiverId: String,
        actorId: String,
        actorName: String?,
        actorAvatarUrl: String?,
        postId: String
    ) {
        sendNotification(
            mapOf(
                "userId" to receiverId,
                "actorId" to actorId,
                "actorName" to (actorName ?: "Người dùng"),
                "actorAvatarUrl" to (actorAvatarUrl ?: ""),
                "postId" to postId,
                "type" to "comment",
                "message" to "đã bình luận bài viết của bạn 💬",
                "createdAt" to Timestamp.now()
            )
        )
    }
}
