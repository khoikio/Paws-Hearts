package com.example.pawshearts.image

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ImageRepository {

    // Gọi cái service vừa tạo ở bước 3
    private val cloudinaryService = RetrofitCloudinary.instance

    suspend fun uploadImageToCloudinary(imageFile: File): String? {
        return try {
            // 1. Chuẩn bị cái tên preset "paws-hearts"
            // Cái này giống như cái CHÌA KHÓA để mở cửa Cloudinary
            val presetName = "paws-hearts"
            val presetBody = presetName.toRequestBody("text/plain".toMediaTypeOrNull())

            // 2. Gói cái file ảnh lại
            // "image/*" nghĩa là file này là ảnh
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            // 3. Gọi API bắn lên
            val response = cloudinaryService.uploadImage(filePart, presetBody)

            // 4. Trả về cái link ảnh (secure_url)
            // Đây là cái link mày sẽ lưu vào Firestore nè!
            response.secure_url

        } catch (e: Exception) {
            e.printStackTrace() // In lỗi ra Logcat nếu toang
            null
        }
    }
}