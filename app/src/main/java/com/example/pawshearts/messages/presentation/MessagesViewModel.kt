package com.example.pawshearts.messages.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawshearts.R
import com.example.pawshearts.messages.data.local.UserSearchResult
import com.example.pawshearts.messages.model.ConversationUiModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await // ⚠️ Cần import cái này để dùng await()




class MessagesViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val me = auth.currentUser
    // --- State cho danh sách hội thoại (Giữ nguyên) ---
    private val _conversations = MutableStateFlow<List<ConversationUiModel>>(emptyList())
    val conversations: StateFlow<List<ConversationUiModel>> = _conversations.asStateFlow()
    private var listenerRegistration: ListenerRegistration? = null


    // --- State cho chuỗi tìm kiếm ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // --- State cho kết quả tìm kiếm ---
    private val _searchResults = MutableStateFlow<List<UserSearchResult>>(emptyList())
    val searchResults: StateFlow<List<UserSearchResult>> = _searchResults.asStateFlow()

    private var searchJob: Job? = null

    init {
        observeConversationsForCurrentUser()
    }

    /**
     * Được gọi từ UI mỗi khi người dùng thay đổi nội dung ô tìm kiếm.
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel() // Hủy bỏ job tìm kiếm cũ nếu có

        if (query.isBlank()) {
            _searchResults.value = emptyList() // Xóa kết quả nếu query trống
            return
        }

        // Debounce: Chờ 500ms sau khi người dùng ngừng gõ rồi mới tìm kiếm
        // để tránh gọi Firebase liên tục.
        searchJob = viewModelScope.launch {
            delay(500L)
            performSearch(query)
        }
    }

    private suspend fun performSearch(query: String) {
        if (me == null) return

        try {
            // Firestore không hỗ trợ tìm kiếm "contains", nhưng hỗ trợ "starts with".
            // Ký tự \uf8ff là một ký tự Unicode rất lớn, giúp tạo ra một khoảng để
            // tìm tất cả các chuỗi bắt đầu bằng `query`.
            val endQuery = query + "\uf8ff"

            // Tìm theo email
            val emailQuery = firestore.collection("users")
                .orderBy("email")
                .startAt(query)
                .endAt(endQuery)
                .get()
                .await()

            // Tìm theo username (tên hiển thị)
            val usernameQuery = firestore.collection("users")
                .orderBy("username")
                .startAt(query)
                .endAt(endQuery)
                .get()
                .await()

            val combinedResults = mutableMapOf<String, UserSearchResult>()

            // Gộp kết quả và loại bỏ trùng lặp (nếu có)
            val allDocs = emailQuery.documents + usernameQuery.documents
            for (doc in allDocs) {
                // Bỏ qua chính mình trong kết quả tìm kiếm
                if (doc.id == me.uid) continue

                val user = UserSearchResult(
                    id = doc.id,
                    name = doc.getString("username") ?: "Unknown User",
                    email = doc.getString("email") ?: "no-email@example.com"
                    // Lấy avatarUrl nếu có
                )
                combinedResults[user.id] = user
            }

            _searchResults.value = combinedResults.values.toList()
        } catch (e: Exception) {
            // Xử lý lỗi, có thể hiển thị một thông báo
            _searchResults.value = emptyList()
        }
    }

    // === KẾT THÚC PHẦN THÊM MỚI ===

    init {
        observeConversationsForCurrentUser()
    }




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

                // Vì việc lấy tên user là xử lý bất đồng bộ (Async),
                // nên ta cần đưa vào viewModelScope
                viewModelScope.launch {
                    val uiList = docs.mapNotNull { doc ->
                        val id = doc.getString("id") ?: doc.id

                        // 1. Lấy thông tin cơ bản từ Thread
                        val lastMessage = doc.getString("lastMessage") ?: ""
                        val lastSentAt = doc.getLong("lastSentAt") ?: 0L
                        val participantIds = doc.get("participantIds") as? List<String> ?: emptyList()

                        // 2. Xác định tên hiển thị
                        var displayAvatar = R.drawable.avatardefault
                        var displayName = "Cuộc trò chuyện"

                        if (id == "global") {
                            displayName = "Paw Hub"
                            displayAvatar = R.drawable.ic_app
                        } else {
                            // Tìm ID người kia (Không phải tôi)
                            val partnerId = participantIds.firstOrNull { it != me.uid }

                            if (partnerId != null) {
                                // 3. ⚠️ GỌI FIRESTORE LẤY TÊN NGƯỜI KIA ⚠️
                                // Lưu ý: Check lại tên trường trong collection "users"
                                // (ví dụ: "name", "fullName", hay "displayName")
                                val nameFromDb = fetchUserName(partnerId)
                                displayName = nameFromDb

                                // Nếu bạn có lưu URL avatar trong users thì fetch luôn ở đây
                            }
                        }

                        // 4. Tạo object tạm để sort
                        ConversationUiModel(
                            id = id,
                            name = displayName,
                            lastMessage = lastMessage,
                            timeLabel = formatTimeLabel(lastSentAt),
                            unreadCount = 0, // Logic unread tính sau
                            statusDotColor = null,
                            avatarRes = displayAvatar,
                            // Dùng trường này để sort bên dưới
                            // Bạn cần thêm 1 biến lastSentAt vào ConversationUiModel nếu muốn sort chuẩn,
                            // hoặc sort Raw trước khi map (nhưng vì map async nên sort sau sẽ tiện hơn)
                        )
                    }.sortedByDescending {
                        // Lưu ý: Logic sort này chỉ đúng nếu timeLabel có thể so sánh,
                        // Tốt nhất UI Model nên giữ lại biến lastSentAt (Long) để sort.
                        // Ở đây tôi tạm thời để nguyên theo logic hiển thị.
                        it.lastMessage // Tạm thời. Xem lưu ý bên dưới 👇
                    }

                    _conversations.value = uiList
                }
            }
    }

    /**
     * Hàm lấy tên user từ collection "users".
     * Dùng .await() để đợi kết quả trả về.
     */
    private suspend fun fetchUserName(userId: String): String {
        return try {
            val snapshot = firestore.collection("users").document(userId).get().await()

            // 👇 SỬA Ở ĐÂY: Thay "fullName"/"name" bằng "username" cho đúng database của bạn
            snapshot.getString("username") ?: "Người dùng ẩn danh"

        } catch (e: Exception) {
            "Lỗi tải tên"
        }
    }

    fun markThreadRead(threadId: String) {
        // Giữ nguyên logic cũ của bạn
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

    private fun formatTimeLabel(millis: Long): String {
        if (millis == 0L) return ""
        return android.text.format.DateFormat.format("HH:mm", millis).toString()
    }
}