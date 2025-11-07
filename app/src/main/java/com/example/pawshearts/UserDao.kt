package com.example.pawshearts.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile") // 1. Đánh dấu đây là một Entity và đặt tên bảng
data class UserData(
    @PrimaryKey val userId: String, // 2. Đánh dấu đây là khóa chính, không được trùng lặp
    val username: String?,
    val profilePictureUrl: String?,
    val email: String?,
    val phone: String?,
    val address: String?
)