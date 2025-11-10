package com.example.pawshearts.data.repository

import android.net.Uri
import com.example.pawshearts.auth.AuthResult
import com.example.pawshearts.data.model.Comment
import com.example.pawshearts.data.model.Post
import kotlinx.coroutines.flow.Flow

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
    suspend fun uploadImage(uri: Uri): AuthResult<String>
    fun getPostById(postId: String): Flow<Post?>
}