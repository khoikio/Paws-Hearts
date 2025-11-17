package com.example.pawshearts.auth

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawshearts.data.model.UserData
import com.example.pawshearts.auth.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.pawshearts.navmodel.Routes
import kotlinx.coroutines.delay


class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    val currentUser: FirebaseUser?
        get() = repository.currentUser
    
    val currentUserState: StateFlow<FirebaseUser?> = repository.currentUserStateFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = repository.currentUser
        )
        
    private val _authState = MutableStateFlow<AuthResult<FirebaseUser>?>(null)
    val authState: StateFlow<AuthResult<FirebaseUser>?> = _authState.asStateFlow()

    val isUserLoggedIn: StateFlow<Boolean> = repository.isUserLoggedInFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = repository.currentUser != null
        )
    private val _prefilledCredentials = MutableStateFlow<Pair<String, String>?>(null)
    val prefilledCredentials: StateFlow<Pair<String, String>?> = _prefilledCredentials.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
    
    // HÀM LOGOUT AN TOÀN VỚI DELAY
    fun logoutAndNavigate(navController: NavHostController) {
        navController.navigate(Routes.LOGIN_SCREEN) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
            launchSingleTop = true
        }
        viewModelScope.launch {
            delay(100) // Thêm delay nhỏ để đảm bảo UI đã hủy xong
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
    private val _userProfile = MutableStateFlow<UserData?>(null)
    val userProfile: StateFlow<UserData?> = _userProfile.asStateFlow()

    init {
        currentUser?.uid?.let { userId ->
            viewModelScope.launch {
                repository.refreshUserProfile()
            }
        }

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
    fun refreshProfile() {viewModelScope.launch {
        Log.d("AuthVM", "Nhận được lệnh refresh profile từ UI...")
        repository.refreshUserProfile()
    }
    }

    fun updatePersonalInfo(email: String, phone: String, address: String) {
        viewModelScope.launch {
            repository.updateUserPersonalInfo(phone, address)
        }
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
            val result = repository.registerWithEmail(email,pass, fullName)
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
