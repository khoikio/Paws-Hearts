package com.example.pawshearts.adopt

import com.google.firebase.Timestamp
import java.util.Date // Cần import Date nếu bạn muốn dùng Date trong ViewModel

data class Comment(
    val id: String = "",
    val postId: String = "",

    val userId: String = "",
    val username: String? = null,
    val userAvatarUrl: String? = null,

    val text: String = "",
    val createdAt: Timestamp = Timestamp.Companion.now()
)