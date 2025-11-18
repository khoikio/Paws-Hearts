// com/example/pawshearts/messages/data/local/MessageEntity.kt
package com.example.pawshearts.messages.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.pawshearts.messages.model.MessageStatus

@Entity(
    tableName = "messages",
    indices = [
        Index(value = ["threadId"]),
        Index(value = ["threadId", "sentAt"])
    ]
)
data class MessageEntity(
    @PrimaryKey
    val id: String,             // messageId = Firestore docId

    val threadId: String,       // global / uidA_uidB
    val senderId: String,
    val senderName: String?,
    val text: String,
    val sentAt: Long,           // epoch millis
    val status: MessageStatus
)
