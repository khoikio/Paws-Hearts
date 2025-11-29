package com.example.pawshearts.data.model


import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date // Quan trọng: Phải import java.util.Date

// Cái @DocumentId là để Firebase tự điền ID cho mỗi document
data class Activity(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "", // Ví dụ: "20/12/2025"
    val location: String = "",
    val contactLink: String = "", // Link Google Form hoặc link ngoài
    val timestamp: Long = System.currentTimeMillis(),
    val imageUrl: String = "",


// Dùng ServerTimestamp để Firebase tự điền thời gian, rất nên dùng
//@ServerTimestamp
//val timestamp: Date? = null
)