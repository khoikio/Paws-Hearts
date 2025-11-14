package com.example.pawshearts.adopt

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Adopt(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatarUrl: String? = null,

    val petName: String = "",
    val petBreed: String = "",
    val petAge: Long = 0L, // Sửa thành Long để tương thích với Firestore
    val petWeight: Double = 0.0,
    val petGender: String = "",
    val petLocation: String = "",
    val description: String = "",
    val imageUrl: String? = null,

    // --- BỔ SUNG CHỨC NĂNG TƯƠNG TÁC ---
    val likeCount: Long = 0L, // Sửa thành Long
    val commentCount: Long = 0L, // Sửa thành Long
    val shareCount: Int = 0,

    @ServerTimestamp
    val timestamp: Date? = null
)