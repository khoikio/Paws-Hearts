package com.example.pawshearts.data.model


data class Notification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "",         // "chat", "activity", "system"
    val senderId: String = "",
    val receiverId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)