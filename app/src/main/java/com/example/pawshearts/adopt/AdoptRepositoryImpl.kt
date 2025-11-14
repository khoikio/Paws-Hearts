package com.example.pawshearts.adopt

import android.util.Log
import com.example.pawshearts.auth.AuthResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FieldValue
import com.example.pawshearts.adopt.Adopt


// ⚠️ Class này phải triển khai (implement) AdoptRepository (interface) với 7 hàm
class AdoptRepositoryImpl(
    private val firestore: FirebaseFirestore
) : AdoptRepository {

    // --- 1. LẤY BÀI ĐĂNG CỦA TÔI (FLOW) ---
    override fun getMyAdoptPostsFlow(userId: String): Flow<List<Adopt>> {
        return callbackFlow {
            if (userId.isBlank()) {
                trySend(emptyList())
                close()
                return@callbackFlow
            }

            val listener = firestore.collection("adopts")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val myAdoptPosts = snapshot?.toObjects(Adopt::class.java) ?: emptyList()
                    trySend(myAdoptPosts)
                }

            awaitClose {
                listener.remove()
            }
        }
    }

    // --- 2. LẤY TẤT CẢ BÀI ĐĂNG (FLOW) ---
    override fun getAllAdoptPostsFlow(): Flow<List<Adopt>> {
        return callbackFlow {
            val listener = firestore.collection("adopts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val allAdoptPosts = snapshot?.toObjects(Adopt::class.java) ?: emptyList()
                    trySend(allAdoptPosts)
                }

            awaitClose {
                listener.remove()
            }
        }
    }

    // --- 3. HÀM TẠO BÀI ĐĂNG MỚI (SUSPEND) ---
    override suspend fun createAdoptPost(adoptPost: Adopt): AuthResult<Unit> {
        return try {
            firestore.collection("adopts").add(adoptPost).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            // Đã trả về AuthResult.Error
            AuthResult.Error(e.message ?: "Lỗi đéo biết KKK :v")
        }
    }

    // --- 4. KIỂM TRA TRẠNG THÁI LIKE (SUSPEND) ---
    override suspend fun checkIfUserLiked(postId: String, userId: String): Boolean {
        return try {
            val likeRef = firestore.collection("adopts")
                .document(postId)
                .collection("likes")
                .document(userId)
            val snapshot = likeRef.get().await()
            snapshot.exists()
        } catch (e: Exception) {
            Log.e("AdoptRepoImpl", "Lỗi kiểm tra trạng thái like KKK", e)
            false // Đã trả về Boolean (false)
        }
    }

    // --- 5. CẬP NHẬT TRẠNG THÁI LIKE (SUSPEND - Unit) ---
    override suspend fun updatePostLikeStatus(postId: String, userId: String, isLiking: Boolean) {
        val likeRef = firestore.collection("adopts")
            .document(postId)
            .collection("likes")
            .document(userId)

        if (isLiking) {
            likeRef.set(mapOf("timestamp" to FieldValue.serverTimestamp())).await()
        } else {
            likeRef.delete().await()
        }
    }

    // --- 6. HÀM THÊM COMMENT (SUSPEND) ---
    override suspend fun addComment(comment: Comment): AuthResult<Unit> {
        return try {
            firestore.collection("adopts")
                .document(comment.postId)
                .collection("comments")
                .add(comment)
                .await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("AdoptRepoImpl", "Lỗi thêm comment KKK", e)
            AuthResult.Error(e.message ?: "Lỗi đéo thêm comment được :@") // Đã trả về AuthResult.Error
        }
    }

    // --- 7. HÀM LẤY COMMENTS (FLOW) ---
    override fun getCommentsFlow(postId: String): Flow<List<Comment>> {
        return callbackFlow {
            if (postId.isBlank()) {
                trySend(emptyList())
                close()
                return@callbackFlow
            }

            val listener = firestore.collection("adopts")
                .document(postId)
                .collection("comments")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val comments = snapshot?.toObjects(Comment::class.java) ?: emptyList()
                    trySend(comments)
                }

            awaitClose {
                listener.remove()
            }
        }
    }
}