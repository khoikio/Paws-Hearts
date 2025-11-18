package com.example.pawshearts.messages.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawshearts.R
import com.example.pawshearts.messages.model.ConversationUiModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MessagesViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _conversations = MutableStateFlow<List<ConversationUiModel>>(emptyList())
    val conversations: StateFlow<List<ConversationUiModel>> = _conversations.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        observeConversationsForCurrentUser()
    }

    /**
     * Lắng nghe collection "threads" trên Firestore,
     * lấy tất cả thread mà current user là participant.
     */
    private fun observeConversationsForCurrentUser() {
        val me = auth.currentUser ?: return

        listenerRegistration = firestore.collection("threads")
            .whereArrayContains("participantIds", me.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _conversations.value = emptyList()
                    return@addSnapshotListener
                }

                val docs = snapshot?.documents ?: emptyList()

                // Lấy dữ liệu thô + sort theo thời gian mới nhất
                val sorted = docs
                    .mapNotNull { doc ->
                        val id = doc.getString("id") ?: doc.id
                        val lastMessage = doc.getString("lastMessage") ?: ""
                        val lastSentAt = doc.getLong("lastSentAt") ?: 0L

                        RawConversation(
                            id = id,
                            lastMessage = lastMessage,
                            lastSentAt = lastSentAt
                        )
                    }
                    .sortedByDescending { it.lastSentAt }

                // Map sang UI model
                val uiList = sorted.map { raw ->
                    val name = when {
                        raw.id == "global" -> "Paw Hub"
                        else -> "Cuộc trò chuyện"
                    }

                    ConversationUiModel(
                        id = raw.id,
                        name = name,
                        lastMessage = raw.lastMessage,
                        timeLabel = formatTimeLabel(raw.lastSentAt),
                        unreadCount = 0,
                        statusDotColor = null,
                        avatarRes = if (raw.id == "global") R.drawable.ic_app else R.drawable.avatardefault
                    )
                }

                _conversations.value = uiList
            }
    }

    /**
     * Đánh dấu 1 thread là đã đọc (local).
     * Sau này nếu muốn sync unreadCount lên server thì chỉnh ở đây.
     */
    fun markThreadRead(threadId: String) {
        viewModelScope.launch {
            val current = _conversations.value.toMutableList()
            val index = current.indexOfFirst { it.id == threadId }
            if (index != -1) {
                val old = current[index]
                current[index] = old.copy(unreadCount = 0)
                _conversations.value = current
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    /**
     * Định dạng thời gian hiển thị đơn giản kiểu "HH:mm".
     * Nếu millis = 0L thì trả chuỗi rỗng.
     */
    private fun formatTimeLabel(millis: Long): String {
        if (millis == 0L) return ""
        return android.text.format.DateFormat.format("HH:mm", millis).toString()
    }

    // Model tạm dùng nội bộ để sort theo lastSentAt rồi mới map sang UI
    private data class RawConversation(
        val id: String,
        val lastMessage: String,
        val lastSentAt: Long
    )
}
