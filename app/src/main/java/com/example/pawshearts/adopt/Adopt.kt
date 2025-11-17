package com.example.pawshearts.adopt

import com.google.firebase.Timestamp

data class Adopt(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatarUrl: String? = null,

    val petName: String = "",
    val petBreed: String = "",
    val petAge: Int = 0,
    val petWeight: Double = 0.0,
    val petGender: String = "",
    val petLocation: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val likeCount: Int = 0,

    val createdAt: Timestamp? = null // <-- SỬA DỨT ĐIỂM Ở ĐÂY
)