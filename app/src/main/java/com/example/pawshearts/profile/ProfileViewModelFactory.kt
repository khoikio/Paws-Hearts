package com.example.pawshearts.profile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pawshearts.auth.AuthRepositoryImpl
import com.example.pawshearts.data.local.PawsHeartsDatabase
import com.example.pawshearts.image.RetrofitCloudinary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileViewModelFactory(
    private val userId: String,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            val auth = FirebaseAuth.getInstance()
            val firestore = FirebaseFirestore.getInstance()
            val cloudinaryService = RetrofitCloudinary.instance
            val userDao = PawsHeartsDatabase.getDatabase(application).userDao()
            val repository = AuthRepositoryImpl(
                auth = auth,
                firestore = firestore,
                userDao = userDao,
                // Thay storage bằng cloudinaryService
                cloudinaryService = cloudinaryService
            )
            return ProfileViewModel(userId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
