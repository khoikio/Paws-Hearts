package com.example.pawshearts.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Lớp này định nghĩa cấu trúc dữ liệu cho một Người Dùng (User).
 * @Entity(tableName = "user_profile"): Đánh dấu đây là một bảng trong Room DB tên là "user_profile".
 * @PrimaryKey: Đánh dấu `userId` là khóa chính, không được trùng lặp.
 */
@Entity(tableName = "user_profile")
data class UserData(
    @PrimaryKey
    val userId: String = "",
    val username: String? = null,
    val profilePictureUrl: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null
)