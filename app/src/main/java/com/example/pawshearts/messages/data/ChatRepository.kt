package com.example.pawshearts.messages.data

import com.example.pawshearts.messages.data.local.MessageDao
import com.example.pawshearts.messages.data.local.MessageEntity
import com.example.pawshearts.messages.data.remote.ChatFirebaseDataSource
import com.example.pawshearts.messages.model.MessageStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ChatRepository(
    private val messageDao: MessageDao,
    private val remote: ChatFirebaseDataSource
) {

    // Firestore dùng để lưu metadata thread (danh sách hội thoại)
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // ===== LOCAL – dùng cho ViewModel quan sát =====
    fun observeMessages(threadId: String): Flow<List<MessageEntity>> {
        return messageDao.observeMessagesInThread(threadId)
    }

//    fun observeLastMessagesPerThread(): Flow<List<MessageEntity>> {
//        return messageDao.observeLastMessagesPerThread()
//    }

    // ===== SYNC – Firestore -> Room =====
    /**
     * Bắt đầu đồng bộ 1 thread từ Firestore về Room.
     * Gọi trong ViewModel với viewModelScope.
     */
    fun startSyncThread(threadId: String, scope: CoroutineScope): Job {
        return scope.launch(Dispatchers.IO) {
            remote.observeMessages(threadId).collect { remoteMessages ->
                val entities = remoteMessages.map { dto ->
                    MessageEntity(
                        id = dto.id,
                        threadId = dto.threadId,
                        senderId = dto.senderId,
                        senderName = dto.senderName,
                        text = dto.text,
                        sentAt = dto.sentAt,
                        status = MessageStatus.SENT  // đã nằm trên server
                    )
                }
                messageDao.upsertMessages(entities)
            }
        }
    }

    // ===== SEND – Room -> Firestore =====
    /**
     * Gửi 1 tin nhắn:
     * 1. Lưu local với trạng thái SENDING
     * 2. Gửi Firestore
     * 3. Nếu ok -> update trạng thái SENT
     * 4. Nếu lỗi -> update FAILED
     * 5. Cập nhật metadata thread (lastMessage, participants, ...)
     */
    suspend fun sendMessage(
        threadId: String,
        text: String,
        currentUserId: String,
        currentUserName: String?
    ) {
        val localId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val local = MessageEntity(
            id = localId,
            threadId = threadId,
            senderId = currentUserId,
            senderName = currentUserName,
            text = text,
            sentAt = now,
            status = MessageStatus.SENDING
        )
        messageDao.upsertMessage(local)

        try {
            // Gửi lên Firestore, dùng luôn localId để không bị trùng 2 message
            val remoteMessage = remote.sendMessage(
                threadId = threadId,
                text = text,
                senderId = currentUserId,
                senderName = currentUserName,
                messageId = localId          // <<< quan trọng: giữ id đồng nhất
            )

            // ==== NEW: cập nhật metadata thread cho màn danh sách hội thoại ====
            // Giả sử threadId = "uidA_uidB" (2 userId sắp xếp)
            val participantIds = threadId.split("_").filter { it.isNotBlank() }

            val threadRef = firestore.collection("threads").document(threadId)
            val threadData = mutableMapOf<String, Any>(
                "id" to threadId,
                "lastMessage" to text,
                "lastSentAt" to remoteMessage.sentAt,
                "lastSenderId" to currentUserId,
                "lastSenderName" to (currentUserName ?: "")
            )

            if (participantIds.isNotEmpty()) {
                threadData["participantIds"] = participantIds
            }

            // merge để không ghi đè toàn bộ doc
            threadRef.set(threadData, SetOptions.merge()).await()
            // ================================================================

            val synced = local.copy(
                sentAt = remoteMessage.sentAt,
                status = MessageStatus.SENT
            )
            messageDao.upsertMessage(synced)   // cùng id => REPLACE
        } catch (e: Exception) {
            val failed = local.copy(status = MessageStatus.FAILED)
            messageDao.upsertMessage(failed)
        }


    }
}