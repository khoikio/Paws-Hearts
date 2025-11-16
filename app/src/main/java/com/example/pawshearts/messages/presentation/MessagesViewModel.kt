package com.example.pawshearts.messages.presentation

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.pawshearts.R
import com.example.pawshearts.messages.ui.components.ChatOrange
import com.example.pawshearts.messages.ui.screens.ConversationUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MessagesViewModel : ViewModel() {

    private val _conversations = MutableStateFlow<List<ConversationUiModel>>(emptyList())
    val conversations: StateFlow<List<ConversationUiModel>> = _conversations.asStateFlow()

    fun loadConversations() {
        // Fake data tạm thời
        _conversations.value = listOf(
            ConversationUiModel(
                id = "1",
                name = "Binh Tran",
                lastMessage = "Bạn có rảnh để nhận nuôi bé Poodle không?",
                timeLabel = "5 phút",
                unreadCount = 1,
                avatarRes = R.drawable.avatar1
            ),
            ConversationUiModel(
                id = "2",
                name = "An Nguyen",
                lastMessage = "Tuyệt vời! Cuối tuần này nhé.",
                timeLabel = "1 giờ",
                unreadCount = 0,
                avatarRes = R.drawable.avatardefault
            )
        )
    }
}