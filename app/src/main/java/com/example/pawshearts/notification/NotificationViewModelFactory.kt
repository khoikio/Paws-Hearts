package com.example.pawshearts.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider // cha cua viewmodle cung cap viewmodel

class NotificationViewModelFactory(
    private val repository: NotificationRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T { // sinh ra viewmodel
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) { // kiem tra phai la Notificationviewmidel
            return NotificationViewModel(repository) as T // true _> tiep tuc ; false -> loi
        }
        throw IllegalArgumentException("Unknown ViewModel class") //  false -> loi
    }
}
