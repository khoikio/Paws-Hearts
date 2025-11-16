package com.example.pawshearts.notification


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Entity(tableName = "notification")
data class Notification(
    @PrimaryKey val id: String = "",        // id thông báo (trùng document id Firebase)
    val userId: String = "",               // id người NHẬN thông báo
    val actorId: String? = null,           // id người GÂY RA (thả tim, cmt,…)
    val actorName: String? = null,         // tên người gây ra
    val actorAvatarUrl: String? = null,    // avatar người gây ra
    val type: String = "",                 // loại: like, comment, system,...
    val message: String = "",              // nội dung hiển thị
    val postId: String? = null,            // id bài viết liên quan
    val isRead: Boolean = false,           // đã đọc chưa
    @ServerTimestamp val createdAt: Date? = null // thời gian tạo (Firebase tự fill)
)
