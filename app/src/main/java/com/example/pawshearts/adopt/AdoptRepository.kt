package com.example.pawshearts.adopt

import com.example.pawshearts.auth.AuthResult
import kotlinx.coroutines.flow.Flow

// ⚠️ Tạo mới file AdoptRepository.kt nếu chưa có
interface AdoptRepository {
    // Adopt Posts
    fun getMyAdoptPostsFlow(userId: String): Flow<List<Adopt>>
    fun getAllAdoptPostsFlow(): Flow<List<Adopt>>
    suspend fun createAdoptPost(adoptPost: Adopt): AuthResult<Unit>

    // Like Functionality
    suspend fun checkIfUserLiked(postId: String, userId: String): Boolean
    suspend fun updatePostLikeStatus(postId: String, userId: String, isLiking: Boolean)

    // ⬇️ BỔ SUNG CHỨC NĂNG COMMENT ⬇️
    suspend fun addComment(comment: Comment): AuthResult<Unit>
    fun getCommentsFlow(postId: String): Flow<List<Comment>>
}