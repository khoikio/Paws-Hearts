package com.example.pawshearts.adopt

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore

class AdoptViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdoptViewModel::class.java)) {
            val repository = AdoptRepositoryImpl(FirebaseFirestore.getInstance())
            @Suppress("UNCHECKED_CAST")
            return AdoptViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}