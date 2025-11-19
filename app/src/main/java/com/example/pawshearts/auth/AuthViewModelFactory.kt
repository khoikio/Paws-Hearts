package com.example.pawshearts.auth

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pawshearts.data.local.PawsHeartsDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AuthViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // --- Chuẩn bị đầy đủ 4 linh kiện ---
            val auth = FirebaseAuth.getInstance()
            val firestore = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance()
            val userDao = PawsHeartsDatabase.getDatabase(application).userDao()

            // --- Lắp ráp Repository cho đúng ---
            val repository = AuthRepositoryImpl(
                auth = auth,
                firestore = firestore,
                userDao = userDao,
                storage = storage
            )

            // --- Tạo ViewModel với Repository xịn ---
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
