package com.example.pawshearts.notification

import com.google.firebase.Timestamp

// ĐÂY LÀ "BẢN THIẾT KẾ" CHÍNH THỨC, NẰM CÙNG NHÀ VỚI CÁC FILE KHÁC CỦA TÍNH NĂNG
data class Notification(
    val id: String = "",
    val userId: String = "", // Người nhận thông báo
    val actorId: String? = null, // Người thực hiện hành động
    val actorName: String? = null,
    val actorAvatarUrl: String? = null,
    val type: String = "", // "LIKE", "COMMENT", "FOLLOW", "NEW_POST"
    val message: String = "",
    val postId: String? = null, // ID bài viết liên quan (nếu có)
    val isRead: Boolean = false,
    val createdAt: Timestamp? = null
)
