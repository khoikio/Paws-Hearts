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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AdoptViewModel(
    private val repository: AdoptRepository
) : ViewModel() {

    // ⚠️ Tạm thời dùng FirebaseAuth và giả định giá trị.
    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    // Dữ liệu UI
    private val _allAdoptPostsRaw = MutableStateFlow<List<Adopt>>(emptyList())
    private val _allAdoptPostsUI = MutableStateFlow<List<AdoptPostUI>>(emptyList())
    val allAdoptPostsUI: StateFlow<List<AdoptPostUI>> = _allAdoptPostsUI

    // Dữ liệu cá nhân
    private val _myAdoptPosts = MutableStateFlow<List<Adopt>>(emptyList())
    val myAdoptPosts: StateFlow<List<Adopt>> = _myAdoptPosts

    // Kết quả đăng bài
    private val _createResult = MutableStateFlow<AuthResult<Unit>?>(null)
    val postResult: StateFlow<AuthResult<Unit>?> = _createResult

    // State cho CommentScreen
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments
    // Đã giả định AuthResult.Idle tồn tại (đã sửa ở các bước trước)
    private val _addCommentState = MutableStateFlow<AuthResult<Unit>>(AuthResult.Idle)
    val addCommentState: StateFlow<AuthResult<Unit>> = _addCommentState




    init {
        fetchAllAdoptPosts()
        observeAllAdoptPostsWithLikeStatus()
    }

    data class AdoptPostUI(
        val adopt: Adopt,
        val isLiked: Boolean = false
    )

    // --- FETCH ALL ---
    private fun fetchAllAdoptPosts() {
        viewModelScope.launch {
            repository.getAllAdoptPostsFlow().collect { posts ->
                _allAdoptPostsRaw.value = posts
            }
        }
    }

    // --- KẾT HỢP DỮ LIỆU LIKE ---
    private fun observeAllAdoptPostsWithLikeStatus() {
        viewModelScope.launch {
            _allAdoptPostsRaw.collectLatest { rawPosts ->
                val userId = currentUserId
                val postsWithLikeStatus = rawPosts.map { adopt ->
                    val isLiked = if (userId != null && adopt.id.isNotEmpty())
                        repository.checkIfUserLiked(adopt.id, userId)
                    else false
                    AdoptPostUI(adopt, isLiked)
                }
                _allAdoptPostsUI.value = postsWithLikeStatus
            }
        }
    }

    // --- FETCH MY POSTS ---
    fun fetchMyAdoptPosts(userId: String) {
        viewModelScope.launch {
            repository.getMyAdoptPostsFlow(userId).collect { posts ->
                _myAdoptPosts.value = posts
            }
        }
    }

    // --- CREATE POST ---
    fun createAdoptPost(
        petName: String, petBreed: String, petAge: String, petWeight: String,
        petGender: String, petLocation: String, description: String, imageUri: Uri?
    ) {
        _createResult.value = AuthResult.Loading
        viewModelScope.launch {
            val userId = currentUserId
            // ⚠️ TODO: Thay thế bằng dữ liệu AuthViewModel thực tế
            val userName = "Dummy User"
            val userAvatarUrl = null

            if (userId == null) {
               _createResult.value = AuthResult.Error("Chưa đăng nhập.")
                return@launch
            }

            var downloadUrl: String? = null
            if (imageUri != null) {
                // SỬA LỖI: Dùng hàm đã sửa bên dưới
                downloadUrl = uploadImageToStorage(imageUri)
                if (downloadUrl == null) {
                    _createResult.value = AuthResult.Error("Lỗi upload ảnh.")
                    return@launch
                }
            }

            val adoptPost = Adopt(
                userId = userId,
                userName = userName,
                userAvatarUrl = userAvatarUrl,
                petName = petName,
                petBreed = petBreed,
                petAge = petAge.toLongOrNull() ?: 0L,
                petWeight = petWeight.toDoubleOrNull() ?: 0.0,
                petGender = petGender,
                petLocation = petLocation,
                description = description,
                imageUrl = downloadUrl
            )
            _createResult.value = repository.createAdoptPost(adoptPost)
        }
    }

    // ⬇️ HÀM NÀY ĐÃ ĐƯỢC CHUYỂN SANG BLOCK BODY ĐỂ KHẮC PHỤC LỖI MISSING RETURN ⬇️
    private suspend fun uploadImageToStorage(uri: Uri): String? { // 👈 KHAI BÁO KIỂU TRẢ VỀ RÕ RÀNG
        try {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("adopt_images/${UUID.randomUUID()}")
            imageRef.putFile(uri).await()
            return imageRef.downloadUrl.await().toString() // 👈 RETURN KHI THÀNH CÔNG
        } catch (e: Exception) {
            Log.e("AdoptVM", "Lỗi upload ảnh", e)
            return null // 👈 RETURN KHI THẤT BẠI
        }
    }
    // ⬆️ END SỬA LỖI ⬆️

    // --- TOGGLE LIKE ---
    fun toggleLike(postId: String) {
        val userId = currentUserId
        if (userId == null) return

        viewModelScope.launch {
            val updatedList = _allAdoptPostsUI.value.map { postUI ->
                if (postUI.adopt.id == postId) {
                    val newIsLiked = !postUI.isLiked
                    val newLikeCount = if (newIsLiked) postUI.adopt.likeCount + 1 else postUI.adopt.likeCount - 1
                    postUI.copy(
                        isLiked = newIsLiked,
                        adopt = postUI.adopt.copy(likeCount = newLikeCount)
                    )
                } else {
                    postUI
                }
            }
            _allAdoptPostsUI.value = updatedList
            try {
                val newIsLiked = updatedList.find { it.adopt.id == postId }?.isLiked ?: return@launch
                repository.updatePostLikeStatus(postId, userId, newIsLiked)
            } catch (e: Exception) {
                Log.e("AdoptVM", "Lỗi Like Post", e)
                // TODO: Thực hiện Rollback UI nếu lỗi
            }
        }
    }

    // --- COMMENT LOGIC ---
    fun fetchComments(postId: String) {
        viewModelScope.launch {
            repository.getCommentsFlow(postId).collect { commentsList ->
                _comments.value = commentsList
            }
        }
    }

    fun addComment(postId: String, userId: String, username: String?, userAvatarUrl: String?, text: String) {
        if (text.isBlank()) return

        _addCommentState.value = AuthResult.Loading
        viewModelScope.launch {
            val newComment = Comment(
                postId = postId,
                userId = userId,
                username = username,
                userAvatarUrl = userAvatarUrl,
                text = text
            )
            val result = repository.addComment(newComment)
            _addCommentState.value = result

            if (result is AuthResult.Success) {
                clearAddCommentState()
            }
        }
    }

    fun clearAddCommentState() {
        _addCommentState.value = AuthResult.Idle
    }

    fun onCommentClicked(postId: String) { /* ... */ }
    fun getShareableContent(postId: String): String {
        // Trả về một giá trị mặc định để hoàn thành cú pháp
        return "Hãy nhận nuôi thú cưng này: [Link tới bài đăng $postId]"
    }
    fun resetCreateResult() { _createResult.value = AuthResult.Idle}
}