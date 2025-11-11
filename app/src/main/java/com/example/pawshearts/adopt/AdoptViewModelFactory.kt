package com.example.pawshearts.adopt

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class AdoptViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdoptViewModel::class.java)) {
            // Tạm thời T 'inject' Firebase ở đây
            val firestore = Firebase.firestore
            val repository = AdoptRepositoryImpl(firestore)
            return AdoptViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class KKK :@")
    }
}