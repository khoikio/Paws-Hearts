package com.example.pawshearts.messages.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawshearts.Utils.uriToFile // Import hàm tiện ích của bạn
import com.example.pawshearts.image.ImageRepository
import com.example.pawshearts.messages.data.ChatRepository
import com.example.pawshearts.messages.model.ChatMessageUiModel
import com.example.pawshearts.messages.model.GLOBAL_THREAD_ID
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatViewModel(
    private val repository: ChatRepository,
    private val currentUserId: String,
    private val currentUserName: String?
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    // Repository xử lý upload (đã được nâng cấp để nhận mọi loại file)
    private val imageRepository = ImageRepository()

    private val _messages = MutableStateFlow<List<ChatMessageUiModel>>(emptyList())
    val messages: StateFlow<List<ChatMessageUiModel>> = _messages.asStateFlow()

    // Logic khóa chat khi spam (ngăn gửi nếu chưa được trả lời quá 3 tin)
    val isSendDisabled: StateFlow<Boolean> = _messages.map { msgs ->
        val hasPartnerReplied = msgs.any { !it.isMine }
        val mySentCount = msgs.count { it.isMine }
        !hasPartnerReplied && mySentCount >= 3
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _headerTitle = MutableStateFlow("Đang tải...")
    val headerTitle: StateFlow<String> = _headerTitle.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private var currentThreadId: String? = null
    private var syncJob: Job? = null
    private var localObserveJob: Job? = null

    fun loadThread(threadId: String) {
        if (threadId == currentThreadId) return
        currentThreadId = threadId
        stopAllListeners()
        if (threadId == GLOBAL_THREAD_ID) {
            _headerTitle.value = "Paw Hub"
        } else {
            fetchPartnerName(threadId)
        }
        syncJob = repository.startSyncThread(threadId, viewModelScope)
        localObserveJob = viewModelScope.launch {
            repository.observeMessages(threadId).collect { entities ->
                _messages.value = entities.map { entity ->
                    ChatMessageUiModel(
                        id = entity.id,
                        text = entity.text,
                        time = TimeFormatUtils.formatTime(entity.sentAt),
                        isMine = entity.senderId == currentUserId,
                        status = entity.status,
                        threadId = entity.threadId,
                        type = entity.type
                    )
                }
            }
        }
    }

    private fun fetchPartnerName(threadId: String) {
        viewModelScope.launch {
            try {
                val threadSnap = firestore.collection("threads").document(threadId).get().await()
                var partnerId: String? = null

                if (threadSnap.exists()) {
                    val participantIds = threadSnap.get("participantIds") as? List<String> ?: emptyList()
                    partnerId = participantIds.firstOrNull { it != currentUserId }
                } else {
                    val ids = threadId.split("_")
                    partnerId = ids.firstOrNull { it != currentUserId }
                }

                if (partnerId != null) {
                    val userSnap = firestore.collection("users").document(partnerId).get().await()
                    val name = userSnap.getString("username") ?: "Người dùng ẩn danh"
                    _headerTitle.value = name
                } else {
                    _headerTitle.value = "Cuộc trò chuyện"
                }
            } catch (e: Exception) {
                _headerTitle.value = "Lỗi tải tên"
            }
        }
    }

    // 👇 1. Gửi ẢNH (Cập nhật để gọi hàm uploadFileToCloudinary với mimeType "image/*")
    fun sendImage(context: Context, uri: Uri) {
        val threadId = currentThreadId ?: return

        viewModelScope.launch {
            _toastMessage.value = "Đang xử lý ảnh..."

            try {
                val file = uriToFile(uri, context)

                if (file.exists() && file.length() > 0) {
                    _toastMessage.value = "Đang upload..."

                    // Gọi hàm mới, chỉ định rõ đây là ảnh
                    val imageUrl = imageRepository.uploadFileToCloudinary(file, "image/*")

                    if (imageUrl != null) {
                        repository.sendMessage(
                            threadId = threadId,
                            text = imageUrl,
                            currentUserId = currentUserId,
                            currentUserName = currentUserName,
                            type = "image"
                        )
                        _toastMessage.value = null
                    } else {
                        _toastMessage.value = "Upload thất bại."
                    }
                } else {
                    _toastMessage.value = "Lỗi file ảnh."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _toastMessage.value = "Lỗi: ${e.message}"
            }
        }
    }

    // 👇 2. Gửi TÀI LIỆU (PDF, Doc...) - Hàm Mới
    fun sendFile(context: Context, uri: Uri) {
        val threadId = currentThreadId ?: return

        viewModelScope.launch {
            _toastMessage.value = "Đang xử lý file..."
            try {
                val file = uriToFile(uri, context)

                if (file.exists() && file.length() > 0) {
                    _toastMessage.value = "Đang upload file..."

                    // Upload với mimeType chung cho ứng dụng/tài liệu
                    val fileUrl = imageRepository.uploadFileToCloudinary(file, "application/*")

                    if (fileUrl != null) {
                        repository.sendMessage(
                            threadId = threadId,
                            text = fileUrl, // Link file
                            currentUserId = currentUserId,
                            currentUserName = currentUserName,
                            type = "file"   // Loại tin nhắn là file
                        )
                        _toastMessage.value = null
                    } else {
                        _toastMessage.value = "Upload file thất bại."
                    }
                } else {
                    _toastMessage.value = "Không đọc được file."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _toastMessage.value = "Lỗi gửi file: ${e.message}"
            }
        }
    }

    // 👇 3. Gửi VỊ TRÍ (Google Maps Link) - Hàm Mới
    fun sendLocation(latitude: Double, longitude: Double) {
        val threadId = currentThreadId ?: return

        // Tạo link Google Maps chuẩn
        val mapLink = "https://maps.google.com/?q=$latitude,$longitude"

        viewModelScope.launch {
            repository.sendMessage(
                threadId = threadId,
                text = mapLink,
                currentUserId = currentUserId,
                currentUserName = currentUserName,
                type = "location" // Loại tin nhắn là location
            )
        }
    }

    // Gửi tin nhắn văn bản thường
    fun sendMessage(text: String) {
        val threadId = currentThreadId ?: return
        if (isSendDisabled.value) {
            _toastMessage.value = "Chờ phản hồi để tiếp tục nhắn tin."
            return
        }
        viewModelScope.launch {
            repository.sendMessage(
                threadId = threadId,
                text = text,
                currentUserId = currentUserId,
                currentUserName = currentUserName,
                type = "text"
            )
        }
    }

    fun clearToastMessage() { _toastMessage.value = null }
    fun setTyping(isTyping: Boolean) { _isTyping.value = isTyping }
    fun stopAllListeners() { syncJob?.cancel(); localObserveJob?.cancel(); syncJob = null; localObserveJob = null }
    override fun onCleared() { super.onCleared(); stopAllListeners() }
}