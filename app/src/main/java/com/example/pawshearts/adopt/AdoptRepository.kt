package com.example.pawshearts.adopt

import com.example.pawshearts.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AdoptRepository{
    // hàm lấy list
    fun getMyAdoptPostsFlow(userId: String): Flow<List<Adopt>>
    fun getAllAdoptPostsFlow(): Flow<List<Adopt>>
    // hàm tạo
    suspend fun createAdoptPost(adoptPost: Adopt): AuthResult<Unit>
}