package com.example.pawshearts.image

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ImageRepository {

    private val cloudinaryService = RetrofitCloudinary.instance

    // 👇 SỬA TÊN HÀM & THÊM THAM SỐ mimeType
    suspend fun uploadFileToCloudinary(file: File, mimeType: String = "image/*"): String? {
        return try {
            val presetName = "paws-hearts"
            val presetBody = presetName.toRequestBody("text/plain".toMediaTypeOrNull())

            // 👇 SỬA Ở ĐÂY: Dùng mimeType được truyền vào thay vì cứng nhắc "image/*"
            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())

            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

            // Gọi hàm bên Service (lát nhớ sửa bên Service thành auto/upload nhé)
            val response = cloudinaryService.uploadFile(filePart, presetBody)

            response.secure_url

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}