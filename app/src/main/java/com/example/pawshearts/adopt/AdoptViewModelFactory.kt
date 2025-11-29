package com.example.pawshearts.adopt

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pawshearts.image.RetrofitCloudinary
import com.google.firebase.firestore.FirebaseFirestore

class AdoptViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdoptViewModel::class.java)) {
            // 1. Lấy Firestore
            val firestore = FirebaseFirestore.getInstance()

            // 2. Lấy Cloudinary Service (Cái mới thêm)
            val cloudinaryService = RetrofitCloudinary.instance

            // 3. Nhét cả 2 vào Repository
            // (Lưu ý: Bên file AdoptRepositoryImpl mày phải sửa Constructor nhận đủ 2 món này nha)
            val repository = AdoptRepositoryImpl(firestore, cloudinaryService)

            return AdoptViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}