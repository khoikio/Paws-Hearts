package com.example.pawshearts.notification

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore

class NotificationViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            val firestore = FirebaseFirestore.getInstance()
            val remoteSource = NotificationFirebaseSource(firestore)
            val repository = NotificationRepositoryImpl(remoteSource)
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(app, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
