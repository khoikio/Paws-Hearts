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



class AuthViewModel(
    private val repository: AuthRepository // Chỉ phụ thuộc vào Interface, không phải Implementation
) : ViewModel() {

    // --- TRẠNG THÁI (STATE) CUNG CẤP CHO UI ---

    // Cung cấp người dùng hiện tại từ Firebase Auth (nếu có)
    val currentUser: FirebaseUser?
        get() = repository.currentUser
    private val _authState = MutableStateFlow<AuthResult<FirebaseUser>?>(null)
    val authState: StateFlow<AuthResult<FirebaseUser>?> = _authState.asStateFlow()


    // Cung cấp trạng thái đăng nhập (true/false)
    val isUserLoggedIn: StateFlow<Boolean> = repository.isUserLoggedInFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = repository.currentUser != null
        )
    private val _prefilledCredentials = MutableStateFlow<Pair<String, String>?>(null)
    val prefilledCredentials: StateFlow<Pair<String, String>?> = _prefilledCredentials.asStateFlow()


    // --- CÁC HÀNH ĐỘNG (ACTIONS) TỪ UI ---

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun updateAvatar(uri: Uri) {
        viewModelScope.launch {
            val userId = currentUser?.uid
            if (userId == null) {
                Log.e("AuthViewModel", "Đéo up avatar đc vì M chưa login KKK")
                return@launch
            }

            // Tạm thời T ko báo Loading (M muốn M tự thêm State)
            // NÓ GỌI HÀM XỊN M VỪA CODE BÊN REPO NÈ KKK
            val result = repository.uploadAvatar(userId, uri)

            if (result is AuthResult.Error) {
                Log.e("AuthViewModel", "Lỗi up avatar: ${result.message}")
                // M nên hiện Toast lỗi ở đây
            }

        }
    }
    private val _userProfile = MutableStateFlow<UserData?>(null)
    val userProfile: StateFlow<UserData?> = _userProfile.asStateFlow()

    // Trong AuthViewModel.kt
    init {
        viewModelScope.launch {
            // Lắng nghe trạng thái đăng nhập
            isUserLoggedIn.collect { loggedIn ->
                if (loggedIn && currentUser != null) {
                    // Khi đăng nhập, bắt đầu lắng nghe profile từ Room
                    repository.getUserProfileFlow(currentUser!!.uid).collect { userData ->
                        _userProfile.value = userData
                    }
                } else {
                    // Khi đăng xuất, xóa profile
                    _userProfile.value = null
                }
            }
        }
    }




    // 4. THÊM HÀM CẬP NHẬT INFO CÁ NHÂN (M đã có trong Repo)
    fun updatePersonalInfo(email: String, phone: String, address: String) {
        viewModelScope.launch {
            repository.updateUserPersonalInfo(phone, address)
        }
    }

    // 5. THÊM HÀM CẬP NHẬT PROFILE (TÊN, EMAIL)
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

    // Tạm thời T thêm 2 hàm này cho M (Register/Login bằng Email)
    fun registerWithEmail(email: String, pass: String, fullName: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            val result = repository.registerWithEmail(email,pass, fullName)
            if (result is AuthResult.Success) {
                // Nếu đăng ký OK, M lưu pass/email lại để tự điền
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

    // Hàm set lỗi (khi M nhập form sai)
    fun setAuthError(message: String) {
        _authState.value = AuthResult.Error(message)
    }

    // Xóa tự động điền (sau khi đã điền)
    fun clearPrefilledCredentials() {
        _prefilledCredentials.value = null
    }
}

