package com.example.pawshearts.adopt

import com.google.firebase.Timestamp

// Dùng Firebase FIRESTORE để lưu bình luận cho bài Adopt
data class AdoptComment(
    val id: String = "",
    val adoptPostId: String = "", // <-- ID của bài Adopt nó đang cmt

    val userId: String = "", // <-- ID của người bình luận
    val username: String? = null,
    val userAvatarUrl: String? = null,

    val text: String = "", // <-- Nội dung bình luận
    val createdAt: Timestamp? = null
)