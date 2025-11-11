package com.example.pawshearts.data.model


import com.google.firebase.firestore.DocumentId

// Cái @DocumentId là để Firebase tự điền ID cho mỗi document
data class Activity(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "", // Ví dụ: "20/12/2025"
    val location: String = "",
    val contactLink: String = "", // Link Google Form hoặc link ngoài
    val timestamp: Long = System.currentTimeMillis()
)