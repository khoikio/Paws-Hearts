package com.example.pawshearts.notification

import com.google.firebase.Timestamp
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification")
data class Notification(
    @PrimaryKey val id: String = "",
    val userId: String = "",
    val actorId: String? = null,
    val actorName: String? = null,
    val actorAvatarUrl: String? = null,
    val type: String = "",
    val message: String = "",
    val targetType: String? = null,
    val targetId: String? = null,
    val isPending: Boolean = false,
    val isRead: Boolean = false,
    val createdAt: Timestamp = Timestamp.now(),
    val readAt: Timestamp? = null,
    val extraData: Map<String, Any>? = null
)
