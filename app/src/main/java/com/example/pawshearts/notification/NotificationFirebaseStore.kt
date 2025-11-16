package com.example.pawshearts.notification

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// lưu trên internet (Firebase)
class NotificationFirebaseStore(
    private val firestore: FirebaseFirestore
) {
    private val COLLECTION_NAME = "notifications"

    // lấy thông báo từ Firebase cho 1 user
    suspend fun fetchNotificationsForUser(userId: String): List<Notification> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Notification::class.java)?.copy(
                    id = doc.id // id = document id trên Firestore
                )
            }
        } catch (e: Exception) {
            // nếu lỗi → trả list rỗng
            emptyList()
        }
    }

    // (option) sync trạng thái đã đọc lên Firebase
    suspend fun markAsReadRemote(notificationId: String) {
        // TODO: nếu muốn lưu isRead lên Firebase thì implement
    }

    // (option) xóa 1 thông báo trên Firebase
    suspend fun deleteRemote(notificationId: String) {
        // TODO: nếu muốn xóa trên cloud luôn thì thêm vào
    }

    // (option) xóa tất cả thông báo của 1 user trên Firebase
    suspend fun deleteAllForUserRemote(userId: String) {
        // TODO: nếu còn thời gian thì làm
    }
}
