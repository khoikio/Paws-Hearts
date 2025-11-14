package com.example.pawshearts.auth

sealed class AuthResult<out T> {

    // 1. Trạng thái khởi tạo (Idle) - Dùng data object
    data object Idle : AuthResult<Nothing>()

    // 2. Trạng thái đang tải (Loading)
    data object Loading : AuthResult<Nothing>()

    // 3. Trạng thái thành công (Success)
    data class Success<out T>(val data: T) : AuthResult<T>()

    // 4. Trạng thái lỗi (Error)
    data class Error(val message: String) : AuthResult<Nothing>()

    // XÓA KHỐI companion object {...} BỊ LỖI
}