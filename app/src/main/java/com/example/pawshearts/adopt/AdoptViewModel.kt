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
        Log.d("ADOPT_DEBUG", "AdoptViewModel bắt đầu khởi tạo (init)")
        fetchAllAdoptPosts()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userId.isNotEmpty()) {
            fetchLikedPosts(userId)
        }
        Log.d("ADOPT_DEBUG", "AdoptViewModel khởi tạo xong!")
    }

    private fun fetchAllAdoptPosts() {
        Log.d("ADOPT_DEBUG", "Bắt đầu gọi fetchAllAdoptPosts")
        viewModelScope.launch {
            repository.getAllAdoptPostsFlow().collect { posts ->
                _allAdoptPosts.value = posts
                Log.d("ADOPT_DEBUG", "Đã nhận được ${posts.size} bài đăng nhận nuôi")
            }
        }
    }

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

    fun clearAddCommentState() {
        _addCommentState.value = null
    }

    fun createAdoptPost(
        petName: String,
        petBreed: String,
        petAge: String,
        petWeight: String,
        petGender: String,
        petLocation: String,
        description: String,
        imageUri: Uri?
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            _postResult.value = AuthResult.Error("M đéo login KKK :@")
            return
        }
        val userId = currentUser.uid
        val userName = currentUser.displayName ?: "User đéo tên"
        val userAvatarUrl = currentUser.photoUrl?.toString()

        _postResult.value = AuthResult.Loading

        viewModelScope.launch {
            try {
                val newPostId = repository.getNewAdoptPostId()
                var imageUrl: String? = null

                if (imageUri != null) {
                    val storageRef = FirebaseStorage.getInstance()
                        .getReference("adopt_images/${newPostId}")
                    imageUrl = storageRef.putFile(imageUri).await()
                        .storage.downloadUrl.await().toString()
                }

                val newAdoptPost = Adopt(
                    id = newPostId,
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
                    createdAt = null
                )

                val result = repository.createAdoptPostWithId(newPostId, newAdoptPost)
                _postResult.value = result

            } catch (e: Exception) {
                Log.e("AdoptVM", "Lỗi vcl M ơi", e)
                _postResult.value = AuthResult.Error(e.message ?: "Lỗi đéo biết KKK :v")
            }
        }
    }

    fun resetPostResult() {
        _postResult.value = null
    }

    fun fetchLikedPosts(userId: String) {
        viewModelScope.launch {
            repository.getLikedPostsByUser(userId).collectLatest { likedIds ->
                _likedPostIds.value = likedIds
            }
        }
    }

    fun toggleLike(adoptPostId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            return
        }

        viewModelScope.launch {
            repository.toggleLike(adoptPostId, userId)
        }
    }
}
