package com.example.pawshearts.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawshearts.auth.AuthRepository
import com.example.pawshearts.auth.AuthResult
import com.example.pawshearts.data.model.UserData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userId: String,
    private val authRepository: AuthRepository
) : ViewModel() {

    val userProfile: StateFlow<UserData?> = authRepository.getUserProfileFlow(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        // KHI VIEWMODEL ĐƯỢC TẠO, NÓ SẼ TỰ ĐỘNG ĐI LẤY DATA MỚI NHẤT
        viewModelScope.launch {
            authRepository.fetchAndCacheUserProfile(userId)
        }
    }

    fun toggleFollow() {
        viewModelScope.launch {
            val result = authRepository.toggleFollow(userId)
            if (result is AuthResult.Success) {
                authRepository.fetchAndCacheUserProfile(userId)   // target user
            }
        }
    }
}
