package com.example.pawshearts.adopt

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Adopt(
    // ID Tụi nó sẽ tự tạo
    val id: String = "", // Firebase tự điền
    val userId: String = "", // ID
    val userName: String = "", // Tên
    val userAvatarUrl: String? = null, // Avatar

    // Thông tin Pet M nhập KKK
    val petName: String = "",
    val petBreed: String = "", // Giống
    val petAge: Int = 0, // Tuổi (tháng)
    val petWeight: Double = 0.0, // Cân nặng (kg)
    val petGender: String = "", // Giới tính
    val petLocation: String = "", // Khu vực
    val description: String = "", // Mô tả
    val imageUrl: String? = null, // Ảnh Pet

    @ServerTimestamp
    val timestamp: Date? = null // Giờ đăng

)