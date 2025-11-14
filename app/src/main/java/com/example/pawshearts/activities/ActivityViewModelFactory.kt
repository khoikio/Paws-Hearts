package com.example.pawshearts.activities

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// CÁI NÀY LÀ CÁI "NHÀ MÁY" ĐỂ M 'inject' CÁI REPO VÔ VM KKK
class ActivityViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivityViewModel::class.java)) {
            // Tạm thời T 'inject' Firebase ở đây cho lẹ KKK
            val firestore = Firebase.firestore
            val repository = ActivityRepositoryImpl(firestore)
            return ActivityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class KKK :@")
    }
}