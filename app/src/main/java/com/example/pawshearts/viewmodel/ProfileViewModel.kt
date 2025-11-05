package com.example.pawshearts.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pawshearts.FakeRepository
import com.example.pawshearts.PetPost
import com.example.pawshearts.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileUiState(
    val userProfile: UserProfile? = null,
    val userPosts: List<PetPost> = emptyList(),
    val adoptedPosts: List<PetPost> = emptyList(),
    val selectedTabIndex: Int = 0
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserProfile()
        loadUserPosts()
    }

    private fun loadUserProfile() {
        // TODO: Replace with real data from AuthRepository/UserRepository
        val fakeUser = UserProfile(
            userId = "u1",
            name = "Lê Nguyễn Ánh Nguyệt",
            email = "anhnguyet.le@email.com",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb"
            // Thêm các thông tin khác nếu cần
        )
        _uiState.update { it.copy(userProfile = fakeUser) }
    }

    private fun loadUserPosts() {
        // TODO: Replace with real data from PetRepository
        val allPosts = FakeRepository.getFeed()
        _uiState.update {
            it.copy(
                userPosts = allPosts.filter { post -> post.status != "adopted" },
                adoptedPosts = allPosts.filter { post -> post.status == "adopted" }
            )
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }
}
