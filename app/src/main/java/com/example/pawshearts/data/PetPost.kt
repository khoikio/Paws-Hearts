package com.example.pawshearts.data

data class PetPost(
    val postId: String = "",
    val title: String = "",
    val name: String = "",
    val type: String = "",
    val gender: String = "",
    val ageMonth: Int = 0,           // dùng ageMonth thay cho age
    val weightKg: Double = 0.0,
    val breed: String = "",
    val imageURL: List<String> = emptyList(), // nhiều ảnh
    val description: String = "",
    val location: String = "",
    val status: String = ""
)
