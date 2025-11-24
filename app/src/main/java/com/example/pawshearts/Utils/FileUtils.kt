package com.example.pawshearts.Utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

// Hàm biến đổi Uri (content://...) thành File thật trên máy
fun uriToFile(uri: Uri, context: Context): File {
    // 1. Tạo một file tạm trong bộ nhớ cache của app
    // Đặt tên file là temp_image + số ngẫu nhiên để không trùng
    val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")

    try {
        // 2. Mở luồng đọc dữ liệu từ Uri
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

        // 3. Mở luồng ghi dữ liệu vào file tạm
        val outputStream = FileOutputStream(file)

        // 4. Chép dữ liệu từ Uri sang File
        inputStream?.copyTo(outputStream)

        // 5. Đóng luồng cho đỡ tốn Ram
        inputStream?.close()
        outputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    // 6. Trả về file đã có dữ liệu
    return file
}