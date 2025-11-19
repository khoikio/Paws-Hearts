package com.example.pawshearts.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository,
) : ViewModel(){



    fun getNotifications(userId: String): StateFlow<List<Notification>> {
        return repository.getNotifications(userId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            repository.deleteNotification(notificationId)
        }
    }

    // THÊM HÀM MỚI
    fun clearAllNotifications(userId: String) {
        viewModelScope.launch {
            repository.clearAllNotifications(userId)
        }
    }
    fun sendTest() {
        viewModelScope.launch {
            try {
                val store = repository as? NotificationFirebaseStore
                store?.sendTestLikeNotification(
                    receiverId = "UID_CUA_M",
                    actorId = "UID_GAY_RA_SU_KIEN"
                )
            } catch (e: Exception) {
                Log.e("NOTI", "Lỗi test thông báo: ${e.message}", e)
            }
        }
    }


}
