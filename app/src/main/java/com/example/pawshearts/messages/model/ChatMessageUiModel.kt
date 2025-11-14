package com.example.pawshearts.messages.model

import androidx.compose.ui.graphics.Color

enum class MessageStatus {
    SENDING,   // đang gửi
    SENT,      // đã gửi
    SEEN       // đã xem
}

// Model tin nhắn
data class ChatMessageUiModel(
    val id: String,
    val text: String,
    val time: String,
    val isMine: Boolean,
    val status: MessageStatus = MessageStatus.SENT,
    val avatarRes: Int? = null, // nếu muốn hiển thị avatar riêng
    val bubbleColor: Color? = null // override màu bubble nếu cần
)
