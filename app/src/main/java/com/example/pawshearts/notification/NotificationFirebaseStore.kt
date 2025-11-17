package com.example.pawshearts.notification

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class NotificationFirebaseStore(
    private val firestore: FirebaseFirestore
) : NotificationRepository {

    override fun getNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val listener = firestore.collection("notifications")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.let {
                    trySend(it.toObjects(Notification::class.java))
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun deleteNotification(notificationId: String) {
        try {
            firestore.collection("notifications").document(notificationId).delete().await()
        } catch (e: Exception) {
            // Log lỗi nếu cần
        }
    }

    // THÊM HÀM MỚI
    override suspend fun clearAllNotifications(userId: String) {
        try {
            val querySnapshot = firestore.collection("notifications")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            // Dùng batch write để xóa nhiều document cùng lúc cho hiệu quả
            val batch = firestore.batch()
            for (document in querySnapshot.documents) {
                batch.delete(document.reference)
            }
            batch.commit().await()
        } catch (e: Exception) {
            // Log lỗi nếu cần
        }
    }
}
