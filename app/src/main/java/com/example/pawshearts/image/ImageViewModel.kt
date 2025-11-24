package com.example.pawshearts.image

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File

class ImageViewModel(private val repository: ImageRepository) : ViewModel() {

    fun uploadAvatar(imageFile: File) {
        viewModelScope.launch {
            println("Bắt đầu upload...")

            // 👇 SỬA LẠI TÊN HÀM Ở ĐÂY (uploadImage -> uploadFile)
            // Mặc định nó sẽ hiểu là "image/*" nên không cần truyền tham số thứ 2 cũng được
            val linkAnh = repository.uploadFileToCloudinary(imageFile)

            if (linkAnh != null) {
                println("Link ảnh : $linkAnh")
                // TODO: Gọi tiếp hàm lưu link này vào Firestore
            } else {
                println(" Upload thất bại.")
            }
        }
    }
}