package com.example.pawshearts.post

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pawshearts.notification.NotificationFirebaseSource
import com.google.firebase.firestore.FirebaseFirestore

class PostViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            val firestore = FirebaseFirestore.getInstance()
            val postRepository = PostRepositoryImpl(firestore)
            val notificationSource = NotificationFirebaseSource(firestore)

            return PostViewModel(
                repository = postRepository,
                notificationSource = notificationSource
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

