package com.example.pawshearts.adopt

import com.example.pawshearts.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AdoptRepository{
    fun getMyAdoptPostsFlow(userId: String): Flow<List<Adopt>>

    fun getAllAdoptPostsFlow(
        species: String?,
        minAge: Int?,
        maxAge: Int?,
        location: String?
    ): Flow<List<Adopt>>

    fun getNewAdoptPostId(): String

    suspend fun createAdoptPostWithId(id: String, adoptPost: Adopt): AuthResult<Unit>

    suspend fun getAdoptPostById(postId: String): Adopt?

    // Loại bỏ các hàm Like/Comment
}