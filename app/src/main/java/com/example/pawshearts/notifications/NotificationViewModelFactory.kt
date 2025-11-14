package com.example.pawshearts.notifications

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth

class NotificationViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = NotificationRepositoryImpl()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        return NotificationViewModel(repo, userId) as T
    }
}
