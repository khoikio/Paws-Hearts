// ChatMessageUiModel.kt
package com.example.pawshearts.messages.model

data class ChatMessageUiModel(
    val id: String,
    val text: String,
    val time: String,
    val isMine: Boolean,
    val status: MessageStatus,
    val threadId: String,
    val type: String = "text"
)


