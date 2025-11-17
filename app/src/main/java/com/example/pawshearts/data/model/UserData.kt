package com.example.pawshearts.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

@Entity(tableName = "user_profile")
data class UserData(
    @PrimaryKey val userId: String = "",
    val username: String? = null,
    val email: String? = null,
    val profilePictureUrl: String? = null,
    val phone: String? = null,
    val address: String? = null,
    
    @get:PropertyName("admin") @set:PropertyName("admin")
    var isAdmin: Boolean = false,
    
    // THÊM DÒNG NÀY VÀO ĐỂ HẾT BỊ CẢNH BÁO
    val role: String? = null,

    val followers: List<String> = emptyList(),
    val following: List<String> = emptyList()
)
