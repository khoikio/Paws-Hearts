package com.example.pawshearts.notifications

import com.example.pawshearts.data.model.Notification
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

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
}
