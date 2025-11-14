package com.example.pawshearts.messages.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawshearts.messages.model.ChatMessageUiModel
import com.example.pawshearts.messages.model.MessageStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    application: Application
) : AndroidViewModel(application) {

    // Danh sách tin nhắn
    private val _messages = MutableStateFlow<List<ChatMessageUiModel>>(emptyList())
    val messages: StateFlow<List<ChatMessageUiModel>> = _messages

    // Trạng thái typing (đối phương đang nhập)
    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping

    fun loadThread(threadId: String) {
        // TODO: sau này gọi Room + Firebase
        // Fake data tạm thời
        _messages.value = listOf(
            ChatMessageUiModel(
                id = "1",
                text = "Chào bạn, bé vẫn còn nhé. Bạn có muốn hỏi thêm thông tin gì không?",
                time = "10:33 AM",
                isMine = false,
                status = MessageStatus.SENT
            ),
            ChatMessageUiModel(
                id = "2",
                text = "Chào bạn, mình thấy bạn đăng tin tìm chủ cho bé Corgi, bé còn không ạ?",
                time = "10:32 AM",
                isMine = true,
                status = MessageStatus.SEEN
            )
        )
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            // Tin nhắn mới ban đầu ở trạng thái SENDING
            val newMsg = ChatMessageUiModel(
                id = System.currentTimeMillis().toString(),
                text = text,
                time = "Bây giờ",
                isMine = true,
                status = MessageStatus.SENDING
            )
            _messages.value += newMsg

            // Giả lập gửi thành công sau 1s → đổi sang SENT
            delay(1000)
            _messages.value = _messages.value.map {
                if (it.id == newMsg.id) it.copy(status = MessageStatus.SENT) else it
            }
        }
    }

    // Hàm bật/tắt typing indicator
    fun setTyping(typing: Boolean) {
        _isTyping.value = typing
    }
}