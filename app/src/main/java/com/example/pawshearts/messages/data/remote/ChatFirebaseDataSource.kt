// com/example/pawshearts/messages/data/remote/ChatFirebaseDataSource.kt
package com.example.pawshearts.messages.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await



data class RemoteMessageDto(
    val id: String,
    val threadId: String,
    val senderId: String,
    val senderName: String?,
    val text: String,
    val sentAt: Long   // epoch millis
)
class ChatFirebaseDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private fun messagesCollection(threadId: String) =
        firestore.collection("threads")
            .document(threadId)
            .collection("messages")

    /**
     * Lắng nghe realtime messages trong 1 thread.
     */
    fun observeMessages(threadId: String): Flow<List<RemoteMessageDto>> = callbackFlow {
        val registration = messagesCollection(threadId)
            .orderBy("sentAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        val id = doc.getString("id") ?: doc.id
                        val senderId = doc.getString("senderId") ?: return@mapNotNull null
                        val text = doc.getString("text") ?: ""
                        val senderName = doc.getString("senderName")
                        val sentAt = doc.getLong("sentAt") ?: 0L

                        RemoteMessageDto(
                            id = id,
                            threadId = threadId,
                            senderId = senderId,
                            senderName = senderName,
                            text = text,
                            sentAt = sentAt
                        )
                    }
                    trySend(list).isSuccess
                }
            }

        awaitClose { registration.remove() }
    }

    /**
     * Gửi tin nhắn mới lên Firestore.
     * Trả về RemoteMessageDto đã gửi.
     */
    suspend fun sendMessage(
        threadId: String,
        text: String,
        senderId: String,
        senderName: String?,
        messageId: String,              // <- thêm param
    ): RemoteMessageDto {
        val now = System.currentTimeMillis()

        // dùng CHÍNH id này làm docId
        val docRef = messagesCollection(threadId).document(messageId)

        val remote = RemoteMessageDto(
            id = messageId,
            threadId = threadId,
            senderId = senderId,
            senderName = senderName,
            text = text,
            sentAt = now
        )

        val data = mapOf(
            "id" to remote.id,
            "threadId" to remote.threadId,
            "senderId" to remote.senderId,
            "senderName" to remote.senderName,
            "text" to remote.text,
            "sentAt" to remote.sentAt
        )

        docRef.set(data).await()
        return remote
    }
}
