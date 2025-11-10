package com.example.pawshearts.adopt

data class Adopt(
    val id: Int,
    val title: String,       // ✅ đổi từ name → title
    val description: String,
    val imageUrl: String
)