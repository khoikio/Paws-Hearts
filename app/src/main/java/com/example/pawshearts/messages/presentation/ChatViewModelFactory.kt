package com.example.pawshearts.messages.presentation


import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pawshearts.messages.data.ChatRepository
import com.example.pawshearts.messages.data.local.ChatDatabase
import com.example.pawshearts.messages.data.remote.ChatFirebaseDataSource

class ChatViewModelFactory(
    private val app: Application,
    private val currentUserId: String,
    private val currentUserName: String?
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            val db = ChatDatabase.getInstance(app)
            val dao = db.messageDao()
            val remote = ChatFirebaseDataSource()
            val repo = ChatRepository(dao, remote)
            return ChatViewModel(
                repository = repo,
                currentUserId = currentUserId,
                currentUserName = currentUserName
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
