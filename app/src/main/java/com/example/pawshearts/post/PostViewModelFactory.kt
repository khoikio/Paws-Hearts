package com.example.pawshearts.post

import android.app.Application // <-- M PHẢI CÓ CÁI NÀY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pawshearts.post.PostRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore


class PostViewModelFactory(
    private val application: Application // <-- SỬA DÒNG NÀY: THÊM CÁI NÀY VÔ
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {

            // Giờ M lấy Firestore sau khi M có 'application' -> HẾT CRASH
            val firestore = FirebaseFirestore.getInstance()
            val repository = PostRepositoryImpl(firestore)

            return PostViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}