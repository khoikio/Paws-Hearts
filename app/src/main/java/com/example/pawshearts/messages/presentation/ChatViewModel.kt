package com.example.pawshearts.messages.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawshearts.messages.data.ChatRepository
import com.example.pawshearts.messages.model.ChatMessageUiModel
import com.example.pawshearts.messages.model.GLOBAL_THREAD_ID
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // ⚠️ Nhớ import cái này

class ChatViewModel(
    private val repository: ChatRepository,
    private val currentUserId: String,
    private val currentUserName: String?
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance() // Khởi tạo Firestore

    private val _messages = MutableStateFlow<List<ChatMessageUiModel>>(emptyList())
    val messages: StateFlow<List<ChatMessageUiModel>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    // 👇 THÊM BIẾN NÀY: Để lưu tên hiển thị trên Header
    private val _headerTitle = MutableStateFlow("Đang tải...")
    val headerTitle: StateFlow<String> = _headerTitle.asStateFlow()

    private var currentThreadId: String? = null
    private var syncJob: Job? = null
    private var localObserveJob: Job? = null

    fun loadThread(threadId: String) {
        if (threadId == currentThreadId) return
        currentThreadId = threadId

        stopAllListeners()

        // 1. Xử lý tên hiển thị (Header Title)
        if (threadId == GLOBAL_THREAD_ID) {
            _headerTitle.value = "Paw Hub"
        } else {
            // Nếu là chat riêng, đi tìm tên người kia
            fetchPartnerName(threadId)
        }

        // 2. Sync Firestore -> Room
        syncJob = repository.startSyncThread(threadId, viewModelScope)

        // 3. Observe Room -> UI
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

    // 👇 HÀM MỚI: Logic tìm tên người chat cùng
    private fun fetchPartnerName(threadId: String) {
        viewModelScope.launch {
            try {
                val threadSnap = firestore.collection("threads").document(threadId).get().await()
                var partnerId: String? = null

                if (threadSnap.exists()) {
                    // --- LOGIC CŨ (Dành cho cuộc trò chuyện đã tồn tại) ---
                    // Nếu document thread đã có, lấy partnerId từ danh sách participantIds
                    val participantIds = threadSnap.get("participantIds") as? List<String> ?: emptyList()
                    partnerId = participantIds.firstOrNull { it != currentUserId }
                } else {
                    // --- LOGIC MỚI (Dành cho cuộc trò chuyện CHƯA tồn tại) ---
                    // Nếu chưa có, suy luận partnerId từ chính threadId (định dạng "id1_id2")
                    val ids = threadId.split("_")
                    partnerId = ids.firstOrNull { it != currentUserId }
                }

                // --- LOGIC CHUNG (Sau khi đã có partnerId) ---
                if (partnerId != null) {
                    // Lấy thông tin User từ partnerId đã tìm được
                    val userSnap = firestore.collection("users").document(partnerId).get().await()
                    val name = userSnap.getString("username") ?: "Người dùng ẩn danh"
                    _headerTitle.value = name
                } else {
                    // Trường hợp dự phòng nếu không thể tìm thấy partnerId
                    _headerTitle.value = "Cuộc trò chuyện"
                }
            } catch (e: Exception) {
                _headerTitle.value = "Lỗi tải tên"
            }
        }
    }


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