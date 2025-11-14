package com.example.pawshearts.post

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawshearts.auth.AuthResult
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostViewModel(
    private val repository: PostRepository
) : ViewModel() {

    // 1. State để báo cho UI biết là "Đang đăng..." hay "Lỗi"
    private val _createPostState = MutableStateFlow<AuthResult<Unit>?>(null)
    val createPostState: StateFlow<AuthResult<Unit>?> = _createPostState.asStateFlow()

    // --- THÊM CỤM NÀY ĐỂ GIỮ LIST BÀI ĐĂNG CỦA TÔI ---
    private val _myPosts = MutableStateFlow<List<Post>>(emptyList())
    val myPosts: StateFlow<List<Post>> = _myPosts.asStateFlow()

    // --- THÊM CỤM NÀY CHO HOME SCREEN (TẤT CẢ BÀI ĐĂNG) ---
    private val _allPosts = MutableStateFlow<List<Post>>(emptyList())
    val allPosts: StateFlow<List<Post>> = _allPosts.asStateFlow()

    // Ham Comment
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    // 2. Ô nhớ để báo state ĐĂNG cmt (Loading/Error/Success)
    private val _addCommentState = MutableStateFlow<AuthResult<Unit>?>(null)
    val addCommentState: StateFlow<AuthResult<Unit>?> = _addCommentState.asStateFlow()

    // --- BÀI ĐĂNG ĐANG XEM CHI TIẾT ---
    private val _selectedPost = MutableStateFlow<Post?>(null)
    val selectedPost: StateFlow<Post?> = _selectedPost.asStateFlow()

    fun fetchMyPosts(userId: String) {
        if (userId.isBlank()) return

        viewModelScope.launch {
            repository.getPostsByUserId(userId).collect { posts ->
                _myPosts.value = posts // Cập nhật list
            }
        }
    }

    // ham nay cho home
    fun fetchAllPosts() {
        viewModelScope.launch {
            repository.fetchAllPostsFlow().collect { posts ->
                _allPosts.value = posts // Cập nhật list TẤT CẢ sang home
            }
        }
    }

    fun createPost(
        // Thông tin thằng đăng (M lấy từ AuthViewModel/UserData)
        userId: String,
        username: String?,
        userAvatarUrl: String?,

        // Thông tin con pet (M lấy từ mấy cái OutlinedTextField)
        petName: String,
        petBreed: String?,
        petAge: Int?,
        petGender: String?,
        location: String?,
        weightKg: Double?,
        imageUri: Uri?,
        description: String
    ) {
        viewModelScope.launch {
            // 1. Báo là "Đang tải"
            _createPostState.value = AuthResult.Loading

            // 2. XỬ LÝ ẢNH (Upload nếu có)
            val imageUrl: String
            if (imageUri != null) {
                // Nếu M có chọn ảnh -> T up ảnh
                val uploadResult = repository.uploadImage(imageUri)

                if (uploadResult is AuthResult.Success) {
                    imageUrl = uploadResult.data
                } else {
                    // Up ảnh lỗi -> Báo lỗi KKK
                    _createPostState.value = AuthResult.Error("Lỗi up ảnh: ${(uploadResult as AuthResult.Error).message}")
                    return@launch // Dừng
                }
            } else {
                // Nếu M đéo chọn ảnh
                imageUrl = "" // Link rỗng
            }

            // 3. Tạo object Post xịn (với link ảnh xịn)
            val newPost = Post(
                id = "",
                userId = userId,
                username = username,
                userAvatarUrl = userAvatarUrl,
                createdAt = Timestamp.now(),
                petName = petName,
                petBreed = petBreed,
                petAge = petAge,
                petGender = petGender,
                location = location,
                weightKg = weightKg,
                imageUrl = imageUrl,
                description = description
            )

            // 4. Quăng cho Repository (để lưu vô Firestore)
            val result = repository.createPost(newPost)

            // 5. Báo kết quả
            _createPostState.value = result
        }
    }

    // ham tim, like'
    fun toggleLike(postId: String, userId: String){
        viewModelScope.launch {
            repository.toggleLike(postId, userId)
        }
    }

    // ham comment
    fun fetchComments(postId: String) {
        if (postId.isBlank()) return
        viewModelScope.launch {
            repository.getCommentsFlow(postId).collect { commentList ->
                _comments.value = commentList // Cập nhật list
            }
        }
    }

    /**
     * Hàm này M sẽ gọi khi M bấm nút "Gửi" cmt
     */
    fun addComment(
        postId: String,
        userId: String,
        username: String?,
        userAvatarUrl: String?,
        text: String
    ) {
        viewModelScope.launch {
            // 1. Check xem M gõ chữ chưa KKK
            if (text.isBlank()) {
                _addCommentState.value = AuthResult.Error("M chưa gõ cmt :v")
                return@launch
            }

            // 2. Báo là "Đang gửi..."
            _addCommentState.value = AuthResult.Loading

            // 3. Tạo object Comment
            val newComment = Comment(
                postId = postId,
                userId = userId,
                username = username,
                userAvatarUrl = userAvatarUrl,
                text = text,
                createdAt = Timestamp.now()
            )

            // 4. Quăng cho Repository
            val result = repository.addComment(newComment)

            // 5. Báo kết quả
            _addCommentState.value = result
        }
    }

    fun fetchPostDetails(postId: String) {
        if (postId.isBlank()) return
        viewModelScope.launch {
            repository.getPostById(postId).collect { post ->
                _selectedPost.value = post // Cập nhật bài
            }
        }
    }

    fun clearAddCommentState() {
        _addCommentState.value = null
    }

    // Hàm này để M reset cái state (sau khi M báo lỗi/thành công)
    // 💡 TÊN HÀM NÀY PHẢI KHỚP VỚI LỆNH GỌI TRONG CreatePostScreen.kt
    fun clearCreatePostState() { // <-- Tên hiện tại trong VM
        _createPostState.value = null
    }
}