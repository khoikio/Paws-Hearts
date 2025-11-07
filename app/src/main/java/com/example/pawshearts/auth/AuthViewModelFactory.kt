package com.example.pawshearts.auth

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pawshearts.data.local.PawsHeartsDatabase
import com.example.pawshearts.data.repository.AuthRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Đây là một "nhà máy" sản xuất ra AuthViewModel.
 * Lý do cần nó: AuthViewModel cần một AuthRepository để hoạt động,
 * và chúng ta không thể tạo nó theo cách thông thường.
 * Factory này sẽ chịu trách nhiệm tạo ra tất cả các dependency cần thiết.
 */
class AuthViewModelFactory(
    private val application: Application // Cần Application Context để tạo Database
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Kiểm tra xem hệ thống có đang yêu cầu tạo một AuthViewModel không
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {

            // Bước 1: Tạo ra các "nguyên liệu" từ tầng Data
            val database = PawsHeartsDatabase.getDatabase(application)
            val userDao = database.userDao()
            val firestore = FirebaseFirestore.getInstance()

            // Bước 2: Tạo ra Repository với các nguyên liệu đó
            val repository = AuthRepositoryImpl(userDao, firestore)

            // Bước 3: Tạo ra AuthViewModel với Repository
            return AuthViewModel(repository) as T
        }

        // Nếu hệ thống yêu cầu một ViewModel khác mà Factory này không biết, hãy báo lỗi
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
