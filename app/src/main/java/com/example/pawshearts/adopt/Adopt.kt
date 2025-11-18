package com.example.pawshearts.adopt

import com.google.firebase.Timestamp

data class Adopt(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatarUrl: String? = null,

    val petName: String = "",
    val petBreed: String = "", // Giống/Loài
    val petAge: Int = 0, // Tính bằng tháng
    val petWeight: Double = 0.0,
    val petGender: String = "",
    val petLocation: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val petHealthStatus: String = "",


    // Yêu cầu nhận nuôi (Điều kiện)
    val adoptionRequirements: String = "",

    // Ngày đăng
    val createdAt: Timestamp? = null
)