package com.example.pawshearts.image

// ProfileViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File

class ImageViewModel(private val repository: ImageRepository) : ViewModel() {

    // Hàm này để MainActivity gọi nè
    fun uploadAvatar(imageFile: File) {
        // Bắt đầu chạy ngầm (không làm đơ màn hình)
        viewModelScope.launch {
            println("Bắt đầu upload...")

            // Gọi thằng Repository đi làm việc
            val linkAnh = repository.uploadImageToCloudinary(imageFile)

            if (linkAnh != null) {
                println("Link ảnh : $linkAnh")
                // TODO: Gọi tiếp hàm lưu link này vào Firestore ở đây
                // repository.saveToFirestore(linkAnh)
            } else {
                println(" Upload thất bại.")
            }
        }
    }
}