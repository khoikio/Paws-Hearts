package com.example.pawshearts.messages.model

import androidx.compose.ui.graphics.Color
import com.example.pawshearts.R

data class ConversationUiModel(
    val id: String,
    val name: String,
    val lastMessage: String,
    val timeLabel: String,
    val unreadCount: Int = 0,
    val statusDotColor: Color? = null,
    val avatarRes: Int = R.drawable.avatardefault
)