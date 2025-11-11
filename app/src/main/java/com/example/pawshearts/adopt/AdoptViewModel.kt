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
import java.util.UUID

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
    //  code hàm fetch, hàm create sau
    init {
        fetchAllAdoptPosts()
    }

    // === HÀM TẢI TẤT CẢ KKK ===
    private fun fetchAllAdoptPosts() {
        viewModelScope.launch {
            repository.getAllAdoptPostsFlow().collect { posts ->
                _allAdoptPosts.value = posts
            }
        }
    }
    fun fetchMyAdoptPosts(userId: String) {
        if (userId.isBlank()) {
            _myAdoptPosts.value = emptyList() // Nếu ID rỗng thì trả list rỗng
            return
        }
        viewModelScope.launch {
            // Nó sẽ gọi cái Repo (Đang trả list rỗng M code ở Bước 1 KKK)
            repository.getMyAdoptPostsFlow(userId).collect { posts ->
                _myAdoptPosts.value = posts // Cập nhật list
            }
        }
    }

    fun createAdoptPost(/* ... */) {
        // viewModelScope.launch { ... }
    }
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
                var imageUrl: String? = null

                // 3. NẾU M CÓ CHỌN ẢNH -> T VỚI M UP ẢNH LÊN STORAGE
                if (imageUri != null) {
                    val storageRef = FirebaseStorage.getInstance()
                        .getReference("adopt_images/${UUID.randomUUID()}") // Tên file ngẫu nhiên
                    imageUrl = storageRef.putFile(imageUri).await()
                        .storage.downloadUrl.await().toString()
                    Log.d("AdoptVM", "Up ảnh xịn: $imageUrl")
                }

                // 4. TẠO CÁI "KHUÔN" (OBJECT)
                val newAdoptPost = Adopt(
                    id = "", // Firebase tự điền
                    userId = userId,
                    userName = userName,
                    userAvatarUrl = userAvatarUrl,
                    petName = petName,
                    petBreed = petBreed,
                    petAge = petAge.toIntOrNull() ?: 0, // Chuyển "12" -> 12
                    petWeight = petWeight.toDoubleOrNull() ?: 0.0, // Chuyển "5.5" -> 5.5
                    petGender = petGender,
                    petLocation = petLocation,
                    description = description,
                    imageUrl = imageUrl, // Link ảnh M vừa up
                    timestamp = null // Firebase tự điền
                )

                // 5. QUĂNG CHO REPO KKK
                val result = repository.createAdoptPost(newAdoptPost)
                _postResult.value = result // Báo kết quả (Thành công / Thất bại)

            } catch (e: Exception) {
                Log.e("AdoptVM", "Lỗi vcl M ơi", e)
                _postResult.value = AuthResult.Error(e.message ?: "Lỗi đéo biết KKK :v")
            }
        }
    }

    // === T THÊM CÁI NÀY VÔ LÀ HẾT LỖI ĐỎ KKK ===
    fun resetPostResult() {
        _postResult.value = null
    }
}