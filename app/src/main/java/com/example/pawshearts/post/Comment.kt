package com.example.pawshearts.post

import com.google.firebase.Timestamp

// dung Firebase FIRESTORE
data class Comment(
    val id: String = "",
    val postId: String = "", // <-- ID của bài post nó đang cmt

    val userId: String = "", // <-- ID của thằng cmt
    val username: String? = null,
    val userAvatarUrl: String? = null,

    val text: String = "", // <-- Nội dung cmt
    val createdAt: Timestamp = Timestamp.Companion.now()
)