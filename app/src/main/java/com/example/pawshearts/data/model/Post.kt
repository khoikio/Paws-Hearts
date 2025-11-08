package com.example.pawshearts.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
@Entity(tableName = "posts")
data class Post(
    @PrimaryKey
    val id: String = "", // ID của bài post
    val userId: String = "", // ID của thằng đăng bài :D
    val username: String? = null, // Tên thằng đăng
    val userAvatarUrl: String? = null, // Avatar thằng đăng

    val createdAt: Timestamp = Timestamp.now(), // Thời gian đăng bài


    val petName: String = "",
    val petBreed: String? = null, // Giống
    val petAge: Int? = 0,
    val petGender: String? = null,
    val location: String? = null,
    val weightKg: Double? = 0.0,

    val imageUrl: String = "", // Ảnh pet
    val description: String = "",
    val likes: List<String> = emptyList(), // Danh sách ID thằng like
    val commentCount: Int = 0, // Số lượng bình luận
)


