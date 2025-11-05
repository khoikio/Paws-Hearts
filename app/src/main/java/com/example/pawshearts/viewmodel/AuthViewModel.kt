package com.example.pawshearts.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Lớp này đại diện cho trạng thái của giao diện người dùng (UI State)
// cho màn hình đăng nhập và đăng ký.
data class AuthUiState(
    val email: String = "",
    val pass: String = "",
    val confirmPass: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(pass: String) {
        _uiState.update { it.copy(pass = pass) }
    }

    fun onConfirmPasswordChange(pass: String) {
        _uiState.update { it.copy(confirmPass = pass) }
    }

    fun login() {
        // TODO: Implement login logic with Firebase Auth
        println("Login attempt with email: ${_uiState.value.email} and pass: ${_uiState.value.pass}")
        // Giả lập đăng nhập thành công
        _uiState.update { it.copy(loginSuccess = true) }
    }

    fun register() {
        // TODO: Implement registration logic with Firebase Auth
        if (_uiState.value.pass != _uiState.value.confirmPass) {
            _uiState.update { it.copy(error = "Passwords do not match") }
            return
        }
        println("Register attempt with email: ${_uiState.value.email}")
        // Giả lập đăng ký thành công và tự động đăng nhập
        _uiState.update { it.copy(loginSuccess = true) }
    }

    // Hàm để reset trạng thái lỗi sau khi đã hiển thị
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    // Hàm để reset trạng thái loginSuccess sau khi đã điều hướng
    fun navigationComplete() {
        _uiState.update { it.copy(loginSuccess = false) }
    }
}
