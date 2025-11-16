package com.example.pawshearts.adopt

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawshearts.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.collectLatest
import java.util.UUID
import android.app.Application

class AdoptViewModel(
    private val repository: AdoptRepository
) : ViewModel(){
    // TẠM THỜI TẠO CÁI LIST RỖNG
    private val _myAdoptPosts = MutableStateFlow<List<Adopt>>(emptyList())
    val myAdoptPosts: StateFlow<List<Adopt>> = _myAdoptPosts
    private val _allAdoptPosts = MutableStateFlow<List<Adopt>>(emptyList())
    val allAdoptPosts: StateFlow<List<Adopt>> = _allAdoptPosts
    private val _postResult = MutableStateFlow<AuthResult<Unit>?>(null)
    val postResult: StateFlow<AuthResult<Unit>?> = _postResult
    private val _comments = MutableStateFlow<List<AdoptComment>>(emptyList())
    val comments: StateFlow<List<AdoptComment>> = _comments
    private val _addCommentState = MutableStateFlow<AuthResult<Unit>?>(null)
    private val _likedPostIds = MutableStateFlow<Set<String>>(emptySet())

    val likedPostIds: StateFlow<Set<String>> = _likedPostIds
    val addCommentState: StateFlow<AuthResult<Unit>?> = _addCommentState

    init {
        fetchAllAdoptPosts()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userId.isNotEmpty()) {
            fetchLikedPosts(userId)
        }
    }

    // === HÀM TẢI TẤT CẢ KKK (Giữ nguyên) ===
    private fun fetchAllAdoptPosts() {
        viewModelScope.launch {
            repository.getAllAdoptPostsFlow().collect { posts ->
                _allAdoptPosts.value = posts
            }
        }
    }

    // fetchMyAdoptPosts (Giữ nguyên)
    fun fetchMyAdoptPosts(userId: String) {
        if (userId.isBlank()) {
            _myAdoptPosts.value = emptyList()
            return
        }
        viewModelScope.launch {
            repository.getMyAdoptPostsFlow(userId).collect { posts ->
                _myAdoptPosts.value = posts
            }
        }
    }

    // fetchComments (Giữ nguyên)
    fun fetchComments(adoptPostId: String) {
        viewModelScope.launch {
            try {
                repository.getCommentsForAdoptPost(adoptPostId).collectLatest {
                    _comments.value = it
                }
            } catch (e: Exception) {
                Log.e("AdoptVM", "FATAL: Lỗi khi tải bình luận cho $adoptPostId", e)
            }
        }
    }

    // addComment (Giữ nguyên)
    fun addComment(
        adoptPostId: String,
        userId: String,
        username: String?,
        userAvatarUrl: String?,
        text: String
    ) {
        if (text.isBlank()) return
        _addCommentState.value = AuthResult.Loading

        val newComment = AdoptComment(
            adoptPostId = adoptPostId,
            userId = userId,
            username = username,
            userAvatarUrl = userAvatarUrl,
            text = text
        )

        viewModelScope.launch {
            val result = repository.addComment(newComment)
            _addCommentState.value = result
        }
    }

    // clearAddCommentState (Giữ nguyên)
    fun clearAddCommentState() {
        _addCommentState.value = null
    }


    // === 🛠️ HÀM TẠO BÀI ĐĂNG (ĐÃ CẬP NHẬT LOGIC ID) ===
    fun createAdoptPost(
        petName: String,
        petBreed: String,
        petAge: String,
        petWeight: String,
        petGender: String,
        petLocation: String,
        description: String,
        imageUri: Uri? // Ảnh M chọn
    ) {
        // 1. LẤY INFO USER
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            _postResult.value = AuthResult.Error("M đéo login KKK :@")
            return
        }
        val userId = currentUser.uid
        val userName = currentUser.displayName ?: "User đéo tên"
        val userAvatarUrl = currentUser.photoUrl?.toString()

        // 2. BÁO LÀ "ĐANG ĐĂNG..."
        _postResult.value = AuthResult.Loading

        viewModelScope.launch {
            try {
                // === BƯỚC MỚI 1: TẠO ID TRƯỚC VÀ DÙNG NÓ ===
                val newPostId = repository.getNewAdoptPostId()

                var imageUrl: String? = null

                // 3. NẾU M CÓ CHỌN ẢNH -> T VỚI M UP ẢNH LÊN STORAGE
                if (imageUri != null) {
                    // Dùng ID bài đăng làm tên file (UUID không còn cần thiết)
                    val storageRef = FirebaseStorage.getInstance()
                        .getReference("adopt_images/${newPostId}")
                    imageUrl = storageRef.putFile(imageUri).await()
                        .storage.downloadUrl.await().toString()
                    Log.d("AdoptVM", "Up ảnh xịn: $imageUrl")
                }

                // 4. TẠO CÁI "KHUÔN" (OBJECT) - GÁN ID ĐÃ TẠO
                val newAdoptPost = Adopt(
                    id = newPostId, // <== GÁN ID CHÍNH XÁC VÀO OBJECT
                    userId = userId,
                    userName = userName,
                    userAvatarUrl = userAvatarUrl,
                    petName = petName,
                    petBreed = petBreed,
                    petAge = petAge.toIntOrNull() ?: 0,
                    petWeight = petWeight.toDoubleOrNull() ?: 0.0,
                    petGender = petGender,
                    petLocation = petLocation,
                    description = description,
                    imageUrl = imageUrl,
                    timestamp = null
                )

                // 5. QUĂNG CHO REPO KKK (GỌI HÀM MỚI createAdoptPostWithId)
                val result = repository.createAdoptPostWithId(newPostId, newAdoptPost)
                _postResult.value = result

            } catch (e: Exception) {
                Log.e("AdoptVM", "Lỗi vcl M ơi", e)
                _postResult.value = AuthResult.Error(e.message ?: "Lỗi đéo biết KKK :v")
            }
        }
    }


    // resetPostResult (Giữ nguyên)
    fun resetPostResult() {
        _postResult.value = null
    }

    // fetchLikedPosts (Giữ nguyên)
    fun fetchLikedPosts(userId: String) {
        viewModelScope.launch {
            repository.getLikedPostsByUser(userId).collectLatest { likedIds ->
                _likedPostIds.value = likedIds
                Log.d("AdoptVM", "Cập nhật ${likedIds.size} bài đã Tim KKK")
            }
        }
    }

    // toggleLike (Giữ nguyên)
    fun toggleLike(adoptPostId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.w("AdoptVM", "User chưa đăng nhập, đéo Tim được KKK")
            return
        }

        viewModelScope.launch {
            val result = repository.toggleLike(adoptPostId, userId)
            if (result is AuthResult.Error) {
                Log.e("AdoptVM", "Toggle like thất bại", Exception(result.message))
            }
        }
    }
}