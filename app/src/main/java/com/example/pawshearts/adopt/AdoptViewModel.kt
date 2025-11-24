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
import java.io.File

class AdoptViewModel(
    private val repository: AdoptRepository
) : ViewModel(){

    private val _allAdoptPosts = MutableStateFlow<List<Adopt>>(emptyList())
    val allAdoptPosts: StateFlow<List<Adopt>> = _allAdoptPosts

    // START: BỔ SUNG CHO MYADOPTPOSTSSCREEN
    private val _myAdoptPosts = MutableStateFlow<List<Adopt>>(emptyList())
    val myAdoptPosts: StateFlow<List<Adopt>> = _myAdoptPosts

    fun fetchMyAdoptPosts(userId: String) {
        viewModelScope.launch {
            // Giả định AdoptRepository có hàm getMyAdoptPostsFlow(userId) trả về Flow
            repository.getMyAdoptPostsFlow(userId)
                .collect { posts ->
                    _myAdoptPosts.value = posts
                }
        }
    }
    // END: BỔ SUNG CHO MYADOPTPOSTSSCREEN

    private val _adoptPostDetail = MutableStateFlow<Adopt?>(null)
    val adoptPostDetail: StateFlow<Adopt?> = _adoptPostDetail

    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState

    data class FilterState(
        val species: String? = null,
        val minAge: Int? = null,
        val maxAge: Int? = null,
        val location: String? = null
    )

    init {
        viewModelScope.launch {
            // Lắng nghe thay đổi của bộ lọc và fetch lại danh sách
            _filterState.collectLatest { filter ->
                fetchAllAdoptPosts(filter)
            }
        }
    }

    private fun fetchAllAdoptPosts(filter: FilterState) {
        viewModelScope.launch {
            repository.getAllAdoptPostsFlow(
                species = filter.species,
                minAge = filter.minAge,
                maxAge = filter.maxAge,
                location = filter.location
            ).collect { posts ->
                _allAdoptPosts.value = posts
            }
        }
    }

    fun updateFilter(
        species: String? = _filterState.value.species,
        minAge: Int? = _filterState.value.minAge,
        maxAge: Int? = _filterState.value.maxAge,
        location: String? = _filterState.value.location
    ) {
        // Cập nhật StateFlow filter. Việc này sẽ tự động gọi fetchAllAdoptPosts
        _filterState.value = _filterState.value.copy(
            species = species,
            minAge = minAge,
            maxAge = maxAge,
            location = location
        )
    }

    fun fetchAdoptPostDetail(postId: String) {
        viewModelScope.launch {
            _adoptPostDetail.value = repository.getAdoptPostById(postId)
        }
    }

    fun resetAdoptPostDetail() {
        _adoptPostDetail.value = null
    }

    // Logic CreateAdoptPost (Giữ nguyên)
    private val _postResult = MutableStateFlow<AuthResult<Unit>?>(null)
    val postResult: StateFlow<AuthResult<Unit>?> = _postResult

    fun createAdoptPost(
        petName: String,
        petBreed: String,
        petAge: String,
        petWeight: String,
        petGender: String,
        petLocation: String,
        description: String,
        adoptionRequirements: String,
        imageFile: File? // <--- Nhận File nha (Không phải Uri)
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            _postResult.value = AuthResult.Error("Người dùng chưa đăng nhập.")
            return
        }
        val userId = currentUser.uid
        val userName = currentUser.displayName ?: "User ẩn danh"
        val userAvatarUrl = currentUser.photoUrl?.toString()

        _postResult.value = AuthResult.Loading

        viewModelScope.launch {
            try {
                // 1. UPLOAD ẢNH LÊN CLOUDINARY (Nếu có file)
                var imageUrl: String? = null

                if (imageFile != null) {
                    Log.d("AdoptVM", "📸 Đang upload ảnh Pet...")
                    when (val uploadResult = repository.uploadImage(imageFile)) {
                        is AuthResult.Success -> {
                            imageUrl = uploadResult.data
                            Log.d("AdoptVM", "✅ Upload xong: $imageUrl")
                        }
                        is AuthResult.Error -> {
                            _postResult.value = AuthResult.Error("Lỗi ảnh: ${uploadResult.message}")
                            return@launch
                        }
                        else -> {}
                    }
                }

                // 2. TẠO ID MỚI (Logic cũ của mày)
                val newPostId = repository.getNewAdoptPostId()

                // 3. TẠO OBJECT ADOPT
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
                    imageUrl = imageUrl, // <--- Link Cloudinary nằm ở đây
                    adoptionRequirements = adoptionRequirements
                )

                // 4. LƯU VÀO FIRESTORE
                val result = repository.createAdoptPostWithId(newPostId, newAdoptPost)
                _postResult.value = result

            } catch (e: Exception) {
                _postResult.value = AuthResult.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }

    fun resetPostResult() {
        _postResult.value = null
    }
}