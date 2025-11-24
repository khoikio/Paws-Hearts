package com.example.pawshearts.post

import android.net.Uri
import com.example.pawshearts.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import java.io.File

//interface la luat cho PostReposirory
interface  PostRepository{
    // đẩy bài đăng lên fireStor
    suspend fun createPost(post: Post): AuthResult<Unit>
    // them ham lay bai viet cua mot user cu the
    fun getPostsByUserId(userId: String): Flow<List<Post>>

    // lay het bai bo vao home
    fun fetchAllPostsFlow(): Flow<List<Post>>
    // ham tim, like
    suspend fun toggleLike(postId: String, userId: String)
    // ham comment
    fun getCommentsFlow(postId: String): Flow<List<Comment>>
    suspend fun addComment(comment: Comment): AuthResult<Unit>
    suspend fun uploadImage(imageFile: File): AuthResult<String>    fun getPostById(postId: String): Flow<Post?>
    suspend fun getPostOwnerId(postId: String): String?
}