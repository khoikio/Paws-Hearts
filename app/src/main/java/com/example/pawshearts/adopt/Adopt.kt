package com.example.pawshearts.adopt

import com.google.firebase.Timestamp

data class Adopt(
    val id: String? = null,       // THAY ĐỔI: Làm cho id nullable
    val userId: String? = null,   // THAY ĐỔI: Làm cho userId nullable
    val userName: String? = null,
    val userAvatarUrl: String? = null,

    val petName: String? = null,
    val petBreed: String? = null,
    val petAge: Int? = null,
    val petWeight: Double? = null,
    val petGender: String? = null,
    val petLocation: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val petHealthStatus: String? = null,

    val adoptionRequirements: String? = null,
    val createdAt: Timestamp? = null
)
