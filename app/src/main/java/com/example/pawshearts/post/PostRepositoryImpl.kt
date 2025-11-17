package com.example.pawshearts.post

import android.net.Uri
import android.util.Log
import com.example.pawshearts.auth.AuthResult
import com.example.pawshearts.notification.Notification
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.Date

class PostRepositoryImpl(
    private val firestore: FirebaseFirestore
) : PostRepository {
    object NotificationTypes {
        const val LIKE = "LIKE"
        const val COMMENT = "COMMENT"
        const val SYSTEM = "SYSTEM"
        const val NEW_POST = "NEW_POST"
    }

    override suspend fun createPost(post: Post): AuthResult<Unit> {
        return try {
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser ?: return AuthResult.Error("Bạn chưa đăng nhập")
            val authorId = currentUser.uid
            val userName = currentUser.displayName ?: "Ai đó"

            val newPostRef = firestore.collection("posts").document()
            
            val finalPost = post.copy(
                id = newPostRef.id,
                userId = authorId,
                userName = userName,
                userAvatarUrl = currentUser.photoUrl?.toString(),
                createdAt = Timestamp.now()
            )

            val batch = firestore.batch()
            batch.set(newPostRef, finalPost)

            // TẠM THỜI TẮT NOTI KHI TẠO POST
            /*
            ...
            */

            batch.commit().await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Lỗi không xác định")
        }
    }

    override fun getPostsByUserId(userId: String): Flow<List<Post>> {
        return callbackFlow {
            val listener = firestore.collection("posts")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        trySend(snapshot.toObjects(Post::class.java))
                    }
                }
            awaitClose { listener.remove() }
        }
    }

    override fun fetchAllPostsFlow(): Flow<List<Post>> {
        return callbackFlow {
            val listener = firestore.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        trySend(snapshot.toObjects(Post::class.java))
                    }
                }
            awaitClose { listener.remove() }
        }
    }

    override suspend fun toggleLike(postId: String, userId: String) {
        val postRef = firestore.collection("posts").document(postId)
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return

        try {
            val postSnapshot = postRef.get().await()
            val currentLikes = postSnapshot.get("likes") as? List<String> ?: emptyList()
            
            if (currentLikes.contains(userId)) {
                postRef.update("likes", FieldValue.arrayRemove(userId)).await()
            } else {
                postRef.update("likes", FieldValue.arrayUnion(userId)).await()

                // TẠM THỜI TẮT NOTI KHI LIKE
                /*
                ...
                */
            }
        } catch (e: Exception) {
            Log.e("PostRepoImpl", "Lỗi toggleLike", e)
        }
    }

    override fun getCommentsFlow(postId: String): Flow<List<Comment>> {
        return callbackFlow {
            val listener = firestore.collection("posts").document(postId)
                .collection("comments")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        trySend(snapshot.toObjects(Comment::class.java))
                    }
                }
            awaitClose { listener.remove() }
        }
    }

    // SỬA LẠI HÀM NÀY ĐỂ DEBUG
    override suspend fun addComment(comment: Comment): AuthResult<Unit> {
        return try {
            val currentUser = FirebaseAuth.getInstance().currentUser ?: return AuthResult.Error("Bạn chưa đăng nhập")
            val postRef = firestore.collection("posts").document(comment.postId)
            val commentRef = postRef.collection("comments").document()

            val finalComment = comment.copy(
                id = commentRef.id,
                userId = currentUser.uid,
                username = currentUser.displayName,
                userAvatarUrl = currentUser.photoUrl?.toString(),
                createdAt = Timestamp.now()
            )

            // Chạy riêng lẻ để debug
            Log.d("ADD_COMMENT_DEBUG", "Bước 1: Chuẩn bị tạo comment...")
            commentRef.set(finalComment).await()
            Log.d("ADD_COMMENT_DEBUG", "Bước 1 THÀNH CÔNG: Đã tạo comment document.")

            Log.d("ADD_COMMENT_DEBUG", "Bước 2: Chuẩn bị update commentCount...")
            postRef.update("commentCount", FieldValue.increment(1)).await()
            Log.d("ADD_COMMENT_DEBUG", "Bước 2 THÀNH CÔNG: Đã update commentCount.")

            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("PostRepoImpl", "Lỗi addComment", e)
            AuthResult.Error(e.message ?: "Lỗi không xác định")
        }
    }

    override suspend fun uploadImage(uri: Uri): AuthResult<String> {
        return try {
            val fileName = "posts/${uri.lastPathSegment}_${System.currentTimeMillis()}"
            val imageRef = FirebaseStorage.getInstance().reference.child(fileName)
            imageRef.putFile(uri).await()
            val downloadUrl = imageRef.downloadUrl.await()
            AuthResult.Success(downloadUrl.toString())
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Lỗi không xác định")
        }
    }

    override fun getPostById(postId: String): Flow<Post?> {
        return callbackFlow {
            val listener = firestore.collection("posts").document(postId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    trySend(snapshot?.toObject(Post::class.java))
                }
            awaitClose { listener.remove() }
        }
    }
}
