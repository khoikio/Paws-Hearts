package com.example.pawshearts.adopt

import com.example.pawshearts.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AdoptRepository{
    // hàm lấy list
    fun getMyAdoptPostsFlow(userId: String): Flow<List<Adopt>>
    fun getAllAdoptPostsFlow(): Flow<List<Adopt>>

    // Lắng nghe bình luận theo thời gian thực
    fun getCommentsForAdoptPost(adoptPostId: String): Flow<List<AdoptComment>>

    // Trạng thái Tim của user cho tất cả bài đăng
    fun getLikedPostsByUser(userId: String): Flow<Set<String>> // Flow<Set<AdoptPostId>>

    // Thêm/gỡ Tim
    suspend fun toggleLike(adoptPostId: String, userId: String): AuthResult<Unit>

    // hàm tạo (Tạm thời giữ lại)
    suspend fun createAdoptPost(adoptPost: Adopt): AuthResult<Unit>

    // === 🛠️ BỔ SUNG 2 HÀM MỚI CHO LOGIC TẠO ID TRƯỚC ===

    /**
     * Lấy một ID document mới từ Firestore mà không cần tạo document
     * @return ID document mới (String)
     */
    fun getNewAdoptPostId(): String

    /**
     * Tạo bài đăng bằng cách sử dụng ID đã được chỉ định (SET thay vì ADD)
     */
    suspend fun createAdoptPostWithId(id: String, adoptPost: Adopt): AuthResult<Unit>

    // Thêm bình luận mới
    suspend fun addComment(comment: AdoptComment): AuthResult<Unit>
}