package com.example.pawshearts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawshearts.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    private val authRepository = AuthRepository()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChange(pass: String) {
        _uiState.update { it.copy(pass = pass, error = null) }
    }

    fun onConfirmPasswordChange(pass: String) {
        _uiState.update { it.copy(confirmPass = pass, error = null) }
    }

    fun login() {
        if (isFormInvalid()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.signIn(_uiState.value.email, _uiState.value.pass)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Login failed") }
            }
        }
    }

    fun register() {
        if (isFormInvalid(isRegister = true)) return

        if (_uiState.value.pass != _uiState.value.confirmPass) {
            _uiState.update { it.copy(error = "Mật khẩu không khớp") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.createUser(_uiState.value.email, _uiState.value.pass)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Registration failed") }
            }
        }
    }

    private fun isFormInvalid(isRegister: Boolean = false): Boolean {
        val email = _uiState.value.email
        val pass = _uiState.value.pass
        
        if (email.isBlank() || pass.isBlank() || (isRegister && _uiState.value.confirmPass.isBlank())) {
            _uiState.update { it.copy(error = "Vui lòng điền đầy đủ thông tin") }
            return true
        }
        return false
    }

    // Hàm để reset trạng thái loginSuccess sau khi đã điều hướng
    fun navigationComplete() {
        _uiState.update { it.copy(loginSuccess = false) }
    }
}
