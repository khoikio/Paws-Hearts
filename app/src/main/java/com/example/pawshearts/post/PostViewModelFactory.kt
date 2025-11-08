package com.example.pawshearts.post


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pawshearts.data.repository.PostRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Nhà máy sản xuất PostViewModel
 */
class PostViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Kiểm tra xem hệ thống có đang yêu cầu tạo một PostViewModel không
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {

            // 1. Tạo nguyên liệu (Chỉ cần Firestore)
            val firestore = FirebaseFirestore.getInstance()

            // 2. Tạo Repository
            val repository = PostRepositoryImpl(firestore)

            // 3. Tạo ViewModel
            return PostViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}