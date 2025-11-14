package com.example.pawshearts.data.model

import com.google.firebase.firestore.DocumentId

data class Notification(
    @DocumentId
    val id: String = "",//ID của thông báo  nghen
    val senderId: String = "",//Id người nhận thông báo
    val receiverId: String = "",//còn nì là id mà cái nguười làm mấy cái hành động xong để app tạo thông báo cho người nhận thông báo
    val postId: String="",//id cu bài post như như package post
    val title: String = "",
    val message: String = "",
    val type: Type = Type.UNKNOWN,        // loại thông báo như là từ "chat", "activity", "system"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

{
    // Enum định nghĩa các loại thông báo là cái Type ở trên á ko có là lỗi nghe
    enum class Type {
        NEW_POST,    // Có bài viết mới (gửi cho followers)
        LIKE,        // Có người thích bài viết của bạn
        COMMENT,     // Có người bình luận bài viết của bạn
        UNKNOWN      // Lỗi
    }
}