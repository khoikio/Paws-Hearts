package com.example.pawshearts.notification

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NotificationFirebaseStore(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("notifications")

    // Cung cấp một Flow để ViewModel lắng nghe thay đổi real-time
    fun getNotificationsFlowForUser(userId: String): Flow<List<Notification>> {
        return callbackFlow {
            val listenerRegistration = collection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val notifications = snapshot.toObjects(Notification::class.java)
                        trySend(notifications)
                    }
                }
            awaitClose { listenerRegistration.remove() }
        }
    }

    suspend fun markAsReadRemote(id: String) {
        try {
            collection.document(id).update("read", true).await()
        } catch (e: Exception) {
            // Xử lý lỗi
        }
    }

    suspend fun deleteRemote(id: String) {
        try {
            collection.document(id).delete().await()
        } catch (e: Exception) {
            // Xử lý lỗi
        }
    }

    suspend fun deleteAllForUserRemote(userId: String) {
        try {
            val querySnapshot = collection.whereEqualTo("userId", userId).get().await()
            firestore.runBatch { batch ->
                querySnapshot.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }
            }.await()
        } catch (e: Exception) {
            // Xử lý lỗi
        }
    }
}