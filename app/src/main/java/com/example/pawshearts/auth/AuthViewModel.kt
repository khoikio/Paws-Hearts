package com.example.pawshearts.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    // authState nullable: null = idle (không đang load, không lỗi, không success)
    private val _authState = MutableStateFlow<AuthResult<FirebaseUser>?>(null)
    val authState: StateFlow<AuthResult<FirebaseUser>?> = _authState.asStateFlow()

    private val _isUserLoggedIn = MutableStateFlow(repository.currentUser != null)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    val currentUser: FirebaseUser?
        get() = repository.currentUser

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        val user = repository.currentUser
        _isUserLoggedIn.value = user != null
        if (user != null) {
            _authState.value = AuthResult.Success(user)
        } else {
            _authState.value = null // idle
        }
    }

    // Register with email/password (keeps current behavior)
    fun register(email: String, password: String) {
        when {
            email.isBlank() -> {
                _authState.value = AuthResult.Error("Email không được để trống")
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _authState.value = AuthResult.Error("Email không hợp lệ")
                return
            }
            password.isBlank() -> {
                _authState.value = AuthResult.Error("Mật khẩu không được để trống")
                return
            }
            password.length < 6 -> {
                _authState.value = AuthResult.Error("Mật khẩu phải có ít nhất 6 ký tự")
                return
            }
        }

        _authState.value = AuthResult.Loading
        viewModelScope.launch {
            val result = repository.registerWithEmail(email, password)
            _authState.value = result
            if (result is AuthResult.Success) {
                _isUserLoggedIn.value = true
            }
        }
    }

    // Login with email/password
    fun login(email: String, password: String) {
        when {
            email.isBlank() -> {
                _authState.value = AuthResult.Error("Email không được để trống")
                return
            }
            password.isBlank() -> {
                _authState.value = AuthResult.Error("Mật khẩu không được để trống")
                return
            }
        }

        _authState.value = AuthResult.Loading
        viewModelScope.launch {
            val result = repository.loginWithEmail(email, password)
            _authState.value = result
            if (result is AuthResult.Success) {
                _isUserLoggedIn.value = true
            }
        }
    }

    // Google sign-in
    fun signInWithGoogle(idToken: String) {
        _authState.value = AuthResult.Loading
        viewModelScope.launch {
            val result = repository.signInWithGoogle(idToken)
            _authState.value = result
            if (result is AuthResult.Success) {
                _isUserLoggedIn.value = true
            }
        }
    }

    // NEW: register and switch to login (UI callback on success)
    // - onSuccess will be executed on the main thread after successful registration
    fun registerAndSwitchToLogin(email: String, password: String, onSuccess: () -> Unit) {
        // basic validation re-used
        when {
            email.isBlank() -> {
                _authState.value = AuthResult.Error("Email không được để trống")
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _authState.value = AuthResult.Error("Email không hợp lệ")
                return
            }
            password.isBlank() -> {
                _authState.value = AuthResult.Error("Mật khẩu không được để trống")
                return
            }
            password.length < 6 -> {
                _authState.value = AuthResult.Error("Mật khẩu phải có ít nhất 6 ký tự")
                return
            }
        }

        _authState.value = AuthResult.Loading
        viewModelScope.launch {
            val result = repository.registerWithEmail(email, password)
            if (result is AuthResult.Success) {
                // After successful registration we log out the newly created user
                // so UI can show the login form (you already implemented this flow)
                repository.logout()
                _isUserLoggedIn.value = false
                _authState.value = null // reset to idle
                // Invoke callback on UI to switch to login tab / clear fields
                onSuccess()
            } else {
                // Pass the error back to UI
                _authState.value = result
            }
        }
    }

    fun logout() {
        repository.logout()
        _isUserLoggedIn.value = false
        _authState.value = null // idle
    }

    fun setAuthError(message: String) {
        _authState.value = AuthResult.Error(message)
    }
}