// com/example/pawshearts/messages/presentation/ChatViewModel.kt
package com.example.pawshearts.messages.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawshearts.messages.data.ChatRepository
import com.example.pawshearts.messages.model.ChatMessageUiModel
import com.example.pawshearts.messages.model.MessageStatus
import com.example.pawshearts.messages.presentation.TimeFormatUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository,
    private val currentUserId: String,
    private val currentUserName: String?
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessageUiModel>>(emptyList())
    val messages: StateFlow<List<ChatMessageUiModel>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private var currentThreadId: String? = null

    // job sync Firestore -> Room
    private var syncJob: Job? = null

    // job observe Room -> UI
    private var localObserveJob: Job? = null

    /**
     * Gọi khi mở 1 cuộc trò chuyện.
     * - Bắt đầu đồng bộ từ Firestore về Room
     * - Quan sát Room và đổ ra UI model
     */
    fun loadThread(threadId: String) {
        if (threadId == currentThreadId) return
        currentThreadId = threadId

        // cancel job cũ nếu có
        stopAllListeners()

        // 1. Sync Firestore -> Room
        syncJob = repository.startSyncThread(threadId, viewModelScope)

        // 2. Observe Room -> UI
        localObserveJob = viewModelScope.launch {
            repository.observeMessages(threadId).collect { entities ->
                _messages.value = entities.map { entity ->
                    ChatMessageUiModel(
                        id = entity.id,
                        text = entity.text,
                        time = TimeFormatUtils.formatTime(entity.sentAt),
                        isMine = entity.senderId == currentUserId,
                        status = entity.status,
                        threadId = entity.threadId
                    )
                }
            }
        }
    }

    /**
     * Gửi tin nhắn trong thread hiện tại.
     */
    fun sendMessage(text: String) {
        val threadId = currentThreadId ?: return
        viewModelScope.launch {
            repository.sendMessage(
                threadId = threadId,
                text = text,
                currentUserId = currentUserId,
                currentUserName = currentUserName
            )
        }
    }

    fun setTyping(isTyping: Boolean) {
        _isTyping.value = isTyping
    }

    /**
     * Hủy tất cả job đang lắng nghe (gọi khi rời màn chat / logout).
     */
    fun stopAllListeners() {
        syncJob?.cancel()
        localObserveJob?.cancel()
        syncJob = null
        localObserveJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopAllListeners()
    }
}
