package com.example.pawshearts.auth

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pawshearts.data.local.PawsHeartsDatabase
import com.example.pawshearts.image.RetrofitCloudinary
// 👇 Import cái này (Sửa package nếu mày để chỗ khác)
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // 1. Chuẩn bị nguyên liệu
            val auth = FirebaseAuth.getInstance()
            val firestore = FirebaseFirestore.getInstance()
            val userDao = PawsHeartsDatabase.getDatabase(application).userDao()

            // 👇 LẤY DỊCH VỤ CLOUDINARY RA
            val cloudinaryService = RetrofitCloudinary.instance

            // 2. Lắp ráp vào Repository
            // (Lưu ý: Bên file AuthRepositoryImpl mày phải sửa Constructor cho khớp thứ tự này nha)
            val repository = AuthRepositoryImpl(
                auth = auth,
                firestore = firestore,
                userDao = userDao, // Giữ lại cái này cho mày
                cloudinaryService = cloudinaryService // Thêm cái này vào
            )

            // 3. Tạo ViewModel
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}