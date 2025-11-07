package com.example.pawshearts.data.model

// Nếu bạn cũng muốn lưu các bài đăng vào Room, hãy thêm @Entity giống như UserData.
// Ví dụ: @Entity(tableName = "posts")
data class Post(
    val id: String = "",
    val name: String = "",    val age: Int = 0,
    val breed: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val location: String = ""
)
