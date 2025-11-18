package com.example.pawshearts.auth

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawshearts.data.model.UserData
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.pawshearts.navmodel.Routes
import kotlinx.coroutines.delay

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    val currentUser: FirebaseUser?
        get() = repository.currentUser

    // Ai đang đăng nhập
    val currentUserState: StateFlow<FirebaseUser?> = repository.currentUserStateFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = repository.currentUser
        )

    // Trạng thái login/register
    private val _authState = MutableStateFlow<AuthResult<FirebaseUser>?>(null)
    val authState: StateFlow<AuthResult<FirebaseUser>?> = _authState.asStateFlow()

    // Đã login hay chưa
    val isUserLoggedIn: StateFlow<Boolean> = repository.isUserLoggedInFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = repository.currentUser != null
        )

    // Gợi ý email/pass sau khi register xong
    private val _prefilledCredentials = MutableStateFlow<Pair<String, String>?>(null)
    val prefilledCredentials: StateFlow<Pair<String, String>?> = _prefilledCredentials.asStateFlow()

    // Logout đơn giản (nếu cần dùng ở đâu đó khác)
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }


    // Logout + điều hướng về màn login
    fun logoutAndNavigate(navController: NavHostController) {
        navController.navigate(Routes.LOGIN_SCREEN) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
            launchSingleTop = true
        }
        viewModelScope.launch {
            delay(100)
            repository.logout()
        }
    }

    fun updateAvatar(uri: Uri) {
        viewModelScope.launch {
            val userId = currentUser?.uid ?: return@launch
            val result = repository.uploadAvatar(userId, uri)
            if (result is AuthResult.Error) {
                Log.e("AuthViewModel", "Lỗi up avatar: ${result.message}")
            }
        }
    }

    // Profile user đang đăng nhập (Room cache)
    private val _userProfile = MutableStateFlow<UserData?>(null)
    val userProfile: StateFlow<UserData?> = _userProfile.asStateFlow()

    init {
        // Lần đầu load app, refresh profile user hiện tại
        currentUser?.uid?.let {
            viewModelScope.launch {
                repository.refreshUserProfile()
            }
        }

        // Nghe trạng thái login và sync userProfile từ Room
        viewModelScope.launch {
            repository.isUserLoggedInFlow.collect { isLoggedIn ->
                if (isLoggedIn && currentUser != null) {
                    repository.getUserProfileFlow(currentUser!!.uid).collect { userDataFromRoom ->
                        _userProfile.value = userDataFromRoom
                    }
                } else {
                    _userProfile.value = null
                }
            }
        }
    }

    // Dùng cho ProfileScreen (sau khi follow/unfollow xong)
    fun refreshUserProfile() {
        viewModelScope.launch {
            repository.refreshUserProfile()
        }
    }


    fun refreshProfile() {
        refreshUserProfile()
    }

    fun updateProfile(newName: String, newEmail: String) {
        viewModelScope.launch {
            repository.updateProfile(newName, newEmail)
        }
    }

    fun updateUserPersonalInfo(phone: String, address: String) {
        viewModelScope.launch {
            repository.updateUserPersonalInfo(phone, address)
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            val result = repository.signInWithGoogle(idToken)
            _authState.value = result
        }
    }

    fun registerWithEmail(email: String, pass: String, fullName: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            val result = repository.registerWithEmail(email, pass, fullName)
            if (result is AuthResult.Success) {
                _prefilledCredentials.value = Pair(email, pass)
            }
            _authState.value = result
        }
    }

    fun loginWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            val result = repository.loginWithEmail(email, pass)
            _authState.value = result
        }
    }

    fun setAuthError(message: String) {
        _authState.value = AuthResult.Error(message)
    }

    fun clearPrefilledCredentials() {
        _prefilledCredentials.value = null
    }
}
