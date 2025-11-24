package com.example.pawshearts.image

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CloudinaryService {
    // 👇 Sửa "image" thành "auto" để nhận cả PDF, Video, File...
    @Multipart
    @POST("v1_1/do5kuvliy/auto/upload")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("upload_preset") preset: RequestBody
    ): CloudinaryResponse
}

// Cái khuôn hứng kết quả trả về
data class CloudinaryResponse(
    val secure_url: String?, // Link ảnh (https://...)
    val public_id: String?
)