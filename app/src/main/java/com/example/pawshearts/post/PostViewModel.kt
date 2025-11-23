package com.example.pawshearts.post

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawshearts.auth.AuthResult
import com.example.pawshearts.notification.NotificationFirebaseSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PostViewModel(
    private val repository: PostRepository,
    private val notificationSource: NotificationFirebaseSource
) : ViewModel() {

    private val _createPostState = MutableStateFlow<AuthResult<Unit>?>(null)
    val createPostState: StateFlow<AuthResult<Unit>?> = _createPostState.asStateFlow()

    private val _myPosts = MutableStateFlow<List<Post>>(emptyList())
    val myPosts: StateFlow<List<Post>> = _myPosts.asStateFlow()

    private val _allPosts = MutableStateFlow<List<Post>>(emptyList())
    val allPosts: StateFlow<List<Post>> = _allPosts.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _addCommentState = MutableStateFlow<AuthResult<Unit>?>(null)
    val addCommentState: StateFlow<AuthResult<Unit>?> = _addCommentState.asStateFlow()

    private val _selectedPost = MutableStateFlow<Post?>(null)
    val selectedPost: StateFlow<Post?> = _selectedPost.asStateFlow()

    fun fetchMyPosts(userId: String) {
        if (userId.isBlank()) return
        viewModelScope.launch {
            repository.getPostsByUserId(userId).collect { posts ->
                _myPosts.value = posts
            }
        }
    }

    fun fetchAllPosts() {
        viewModelScope.launch {
            repository.fetchAllPostsFlow().collect { posts ->
                Log.d("DEBUG_POSTS", "📸 Fetched ${posts.size} posts from Firestore")

                _allPosts.value = posts
            }
        }
    }

    fun createPost(
        userId: String,
        username: String?,
        userAvatarUrl: String?,
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
            _createPostState.value = AuthResult.Loading

            val imageUrl = if (imageUri != null) {
                when (val uploadResult = repository.uploadImage(imageUri)) {
                    is AuthResult.Success -> uploadResult.data
                    is AuthResult.Error -> {
                        _createPostState.value = AuthResult.Error("Lỗi upload ảnh: ${uploadResult.message}")
                        return@launch
                    }
                    else -> ""
                }
            } else ""

            val newPost = Post(
                id = "",
                userId = userId,
                userName = username ?: "",
                userAvatarUrl = userAvatarUrl,
                createdAt = null,
                petName = petName,
                petBreed = petBreed,
                petAge = petAge,
                petGender = petGender,
                location = location,
                weightKg = weightKg,
                imageUrl = imageUrl,
                description = description
            )

            val result = repository.createPost(newPost)
            _createPostState.value = result

            // ✅ Gửi thông báo bài đăng mới cho tất cả người dùng (trừ chính mình)
            if (result is AuthResult.Success) {
                val allUsers = FirebaseFirestore.getInstance().collection("users").get().await()
                allUsers.documents.forEach { doc ->
                    val receiverId = doc.id
                    if (receiverId != userId) {
                        notificationSource.sendPostNotification(
                            receiverId = receiverId,
                            actorId = userId,
                            actorName = username ?: "Người dùng",
                            actorAvatarUrl = userAvatarUrl ?: "",
                            postId = newPost.id
                        )
                    }
                }
            }
        }
    }

    fun toggleLike(postId: String, userId: String) {
        viewModelScope.launch {
            repository.toggleLike(postId, userId)
            val postOwnerId = repository.getPostOwnerId(postId)

            if (postOwnerId != null && postOwnerId != userId) {
                val userDoc = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .get()
                    .await()

                val username = userDoc.getString("userName") ?: "Người dùng"
                val userAvatarUrl = userDoc.getString("userAvatarUrl") ?: ""

                notificationSource.sendLikeNotification(
                    receiverId = postOwnerId,
                    actorId = userId,
                    actorName = username,
                    actorAvatarUrl = userAvatarUrl,
                    postId = postId
                )
            }
        }
    }

    fun fetchComments(postId: String) {
        if (postId.isBlank()) return
        viewModelScope.launch {
            repository.getCommentsFlow(postId).collect { _comments.value = it }
        }
    }

    fun addComment(
        postId: String,
        userId: String,
        username: String?,
        userAvatarUrl: String?,
        text: String
    ) {
        viewModelScope.launch {
            if (text.isBlank()) {
                _addCommentState.value = AuthResult.Error("Bạn chưa nhập nội dung bình luận!")
                return@launch
            }

            _addCommentState.value = AuthResult.Loading

            val newComment = Comment(
                postId = postId,
                userId = userId,
                username = username,
                userAvatarUrl = userAvatarUrl,
                text = text,
                createdAt = null
            )

            val result = repository.addComment(newComment)
            _addCommentState.value = result

            if (result is AuthResult.Success) {
                val postOwnerId = repository.getPostOwnerId(postId)
                if (postOwnerId != null && postOwnerId != userId) {
                    notificationSource.sendCommentNotification(
                        receiverId = postOwnerId,
                        actorId = userId,
                        actorName = username ?: "Người dùng",
                        actorAvatarUrl = userAvatarUrl ?: "",
                        postId = postId
                    )
                }
            }
        }
    }

    fun fetchPostDetails(postId: String) {
        if (postId.isBlank()) return
        viewModelScope.launch {
            repository.getPostById(postId).collect { _selectedPost.value = it }
        }
    }

    fun clearAddCommentState() {
        _addCommentState.value = null
    }

    fun clearCreatePostState() {
        _createPostState.value = null
    }
}
