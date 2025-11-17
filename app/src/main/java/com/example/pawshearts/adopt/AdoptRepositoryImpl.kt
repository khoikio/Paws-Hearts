package com.example.pawshearts.adopt

import android.util.Log
import com.example.pawshearts.auth.AuthResult
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FieldValue

class AdoptRepositoryImpl(
    private val firestore: FirebaseFirestore
) : AdoptRepository {
    private val ADOPT_COMMENTS_COLLECTION = "adopt_comments"
    private val USER_LIKES_COLLECTION = "user_likes"
    private val ADOPTS_COLLECTION = "adopt_posts"

    override fun getMyAdoptPostsFlow(userId: String): Flow<List<Adopt>> {
        return callbackFlow {
            if (userId.isBlank()) {
                trySend(emptyList())
                close()
                return@callbackFlow
            }

            val listener = firestore.collection(ADOPTS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("AdoptRepoImpl", "Lỗi nghe MyAdopts", error)
                        close(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        trySend(snapshot.toObjects(Adopt::class.java))
                    } else {
                        trySend(emptyList())
                    }
                }
            awaitClose { listener.remove() }
        }
    }

    override fun getAllAdoptPostsFlow(): Flow<List<Adopt>> {
        return callbackFlow {
            val listener = firestore.collection(ADOPTS_COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("AdoptRepoImpl", "Lỗi nghe AllAdopts", error)
                        close(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        trySend(snapshot.toObjects(Adopt::class.java))
                    } else {
                        trySend(emptyList())
                    }
                }
            awaitClose { listener.remove() }
        }
    }

    override fun getNewAdoptPostId(): String {
        return firestore.collection(ADOPTS_COLLECTION).document().id
    }

    override suspend fun createAdoptPostWithId(id: String, adoptPost: Adopt): AuthResult<Unit> {
        return try {
            val finalPost = adoptPost.copy(createdAt = Timestamp.now())
            firestore.collection(ADOPTS_COLLECTION).document(id).set(finalPost).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("AdoptRepoImpl", "Lỗi createAdoptPostWithId", e)
            AuthResult.Error(e.message ?: "Lỗi không xác định")
        }
    }

    override suspend fun createAdoptPost(adoptPost: Adopt): AuthResult<Unit> {
        return AuthResult.Error("Hàm không dùng nữa.")
    }

    // SỬA LẠI HÀM NÀY CHO AN TOÀN
    override fun getLikedPostsByUser(userId: String): Flow<Set<String>> {
        return callbackFlow {
            if (userId.isBlank()) {
                trySend(emptySet())
                close()
                return@callbackFlow
            }

            val docRef = firestore.collection(USER_LIKES_COLLECTION).document(userId)

            val listener = docRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Nếu có lỗi (VD: PERMISSION_DENIED), log lỗi và gửi về list rỗng
                    Log.w("AdoptRepoImpl", "Lỗi nghe trạng thái Tim, có thể do document chưa tồn tại. Lỗi: ${error.message}")
                    trySend(emptySet()) 
                    // KHÔNG close() flow ở đây để nó có thể thử lại
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val likedList = snapshot.get("likedPostIds") as? List<String> ?: emptyList()
                    trySend(likedList.toSet())
                } else {
                    // Nếu document chưa tồn tại, gửi về list rỗng
                    trySend(emptySet())
                }
            }

            awaitClose {
                listener.remove()
            }
        }
    }


    override suspend fun toggleLike(adoptPostId: String, userId: String): AuthResult<Unit> {
        return try {
            val userDocRef = firestore.collection(USER_LIKES_COLLECTION).document(userId)
            val postDocRef = firestore.collection(ADOPTS_COLLECTION).document(adoptPostId)

            val snapshot = userDocRef.get().await()
            val likedList = snapshot.get("likedPostIds") as? List<String> ?: emptyList()
            val isCurrentlyLiked = likedList.contains(adoptPostId)

            firestore.runBatch { batch ->
                val incrementValue = if (isCurrentlyLiked) -1L else 1L
                batch.update(postDocRef, "likeCount", FieldValue.increment(incrementValue))

                if (isCurrentlyLiked) {
                    batch.update(userDocRef, "likedPostIds", FieldValue.arrayRemove(adoptPostId))
                } else {
                    if (snapshot.exists()) {
                        batch.update(userDocRef, "likedPostIds", FieldValue.arrayUnion(adoptPostId))
                    } else {
                        batch.set(userDocRef, mapOf("likedPostIds" to listOf(adoptPostId)))
                    }
                }
            }.await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Lỗi không xác định")
        }
    }

    override fun getCommentsForAdoptPost(adoptPostId: String): Flow<List<AdoptComment>> {
        return callbackFlow {
            val listener = firestore.collection(ADOPTS_COLLECTION)
                .document(adoptPostId)
                .collection("comments")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        trySend(snapshot.toObjects(AdoptComment::class.java))
                    } else {
                        trySend(emptyList())
                    }
                }
            awaitClose { listener.remove() }
        }
    }

    override suspend fun addComment(comment: AdoptComment): AuthResult<Unit> {
        return try {
            val commentCollectionRef = firestore.collection(ADOPTS_COLLECTION)
                .document(comment.adoptPostId)
                .collection("comments")

            val newCommentRef = commentCollectionRef.document()
            val finalComment = comment.copy(
                id = newCommentRef.id,
                createdAt = Timestamp.now()
            )
            newCommentRef.set(finalComment).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Lỗi không xác định")
        }
    }
}
