package com.example.pawshearts

data class UserProfile(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val avatarUrl: String = "",
    val address: String = "",
    val role: String = "user",
    val createdAt: Long = System.currentTimeMillis()
)

data class PetPost(
    val postId: String = "",
    val ownerId: String = "",
    val title: String = "",
    val description: String = "",
    val type: String = "",        // dog|cat|other
    val gender: String = "",      // male|female|unknown
    val ageMonth: Int = 0,
    val weightKg: Double? = null,
    val location: String = "",
    val status: String = "open",  // open|pending|adopted|lost|found
    val photos: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()

)

data class AdoptionRequest(
    val requestId: String = "",
    val postId: String = "",
    val requesterId: String = "",
    val message: String = "",
    val status: String = "pending", // pending|approved|rejected
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
