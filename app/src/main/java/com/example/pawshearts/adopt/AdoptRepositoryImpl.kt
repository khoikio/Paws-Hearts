package com.example.pawshearts.adopt

import android.util.Log
import com.example.pawshearts.auth.AuthResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.example.pawshearts.adopt.Adopt
import com.google.firebase.firestore.FieldValue

class AdoptRepositoryImpl(
    private val firestore: FirebaseFirestore
) : AdoptRepository {
    private val ADOPT_COMMENTS_COLLECTION = "adopt_comments"
    private val USER_LIKES_COLLECTION = "user_likes"
    private val ADOPTS_COLLECTION = "adopts"

    override fun getMyAdoptPostsFlow(userId: String): Flow<List<Adopt>> {
        return callbackFlow {
            if (userId.isBlank()) {
                Log.w("AdoptRepoImpl", "User ID rỗng, đéo fetch my adopts KKK")
                trySend(emptyList())
                close()
                return@callbackFlow
            }

            Log.d("AdoptRepoImpl", "Bắt đầu 'nghe' (listen) bài nhận nuôi của $userId")
            val listener = firestore.collection("adopts")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        Log.e("AdoptRepoImpl", "Lỗi nghe MyAdopts KKK", error)
                        close(error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val myAdoptPosts = snapshot.toObjects(Adopt::class.java)
                        Log.d("AdoptRepoImpl", "Nghe thấy ${myAdoptPosts.size} bài KKK. Gửi về VM...")
                        trySend(myAdoptPosts)
                    } else {
                        Log.d("AdoptRepoImpl", "Snapshot rỗng, chắc đéo có bài nào :v")
                        trySend(emptyList())
                    }
                }

            awaitClose {
                Log.d("AdoptRepoImpl", "Hủy 'nghe' MyAdopts KKK")
                listener.remove()
            }
        }
    }

    override fun getAllAdoptPostsFlow(): Flow<List<Adopt>> {
        return callbackFlow {
            Log.d("AdoptRepoImpl", "Bắt đầu 'nghe' (listen) TẤT CẢ bài nhận nuôi KKK")

            val listener = firestore.collection("adopts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        Log.e("AdoptRepoImpl", "Lỗi nghe TẤT CẢ Adopts KKK", error)
                        close(error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val allAdoptPosts = snapshot.toObjects(Adopt::class.java)
                        Log.d("AdoptRepoImpl", "Nghe thấy ${allAdoptPosts.size} bài KKK. Gửi về VM...")
                        trySend(allAdoptPosts)
                    } else {
                        trySend(emptyList())
                    }
                }

            awaitClose {
                Log.d("AdoptRepoImpl", "Hủy 'nghe' TẤT CẢ Adopts KKK")
                listener.remove()
            }
        }
    }

    // =========================================================================
    // === PHẦN SỬA: TẠO BÀI ĐĂNG VỚI ID ĐÃ ĐƯỢC CHỈ ĐỊNH TRƯỚC ===

    override fun getNewAdoptPostId(): String {
        // Trả về ID document mới mà không cần lưu
        return firestore.collection(ADOPTS_COLLECTION).document().id
    }

    override suspend fun createAdoptPostWithId(id: String, adoptPost: Adopt): AuthResult<Unit> {
        return try {
            // Dùng set() với ID đã tạo sẵn (đảm bảo ID document = ID trong object)
            firestore.collection(ADOPTS_COLLECTION).document(id).set(adoptPost).await()
            Log.d("AdoptRepoImpl", "Đăng bài nhận nuôi THÀNH CÔNG (Dùng ID đã tạo) KKK :D")
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("AdoptRepoImpl", "Đăng bài nhận nuôi THẤT BẠI KKK :@", e)
            AuthResult.Error(e.message ?: "Lỗi đéo biết KKK :v")
        }
    }

    // BỎ HÀM CŨ: Hàm này không còn được dùng, bạn có thể xóa nó sau khi kiểm tra.
    override suspend fun createAdoptPost(adoptPost: Adopt): AuthResult<Unit> {
        // Tạm thời trả về lỗi để đảm bảo AdoptViewModel sử dụng hàm createAdoptPostWithId mới
        return AuthResult.Error("Phải dùng createAdoptPostWithId.")
    }

    // =========================================================================


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
                    Log.e("AdoptRepoImpl", "Lỗi nghe trạng thái Tim", error)
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    @Suppress("UNCHECKED_CAST")
                    val likedList = snapshot.get("likedPostIds") as? List<String> ?: emptyList()
                    trySend(likedList.toSet())
                } else {
                    trySend(emptySet())
                }
            }

            awaitClose {
                listener.remove()
            }
        }
    }

    // === HÀM THÊM/GỠ TIM (ĐÃ SỬA LỖI BATCH) ===
    override suspend fun toggleLike(adoptPostId: String, userId: String): AuthResult<Unit> {
        return try {
            val userDocRef = firestore.collection(USER_LIKES_COLLECTION).document(userId)
            val postDocRef = firestore.collection(ADOPTS_COLLECTION).document(adoptPostId)

            val snapshot = userDocRef.get().await()
            @Suppress("UNCHECKED_CAST")
            val likedList = snapshot.get("likedPostIds") as? List<String> ?: emptyList()
            val isCurrentlyLiked = likedList.contains(adoptPostId)
            val userDocExists = snapshot.exists()

            firestore.runBatch { batch ->

                val incrementValue = if (isCurrentlyLiked) -1L else 1L
                val postUpdateMap = mapOf("likeCount" to FieldValue.increment(incrementValue))

                val likeMap = if (isCurrentlyLiked) {
                    mapOf("likedPostIds" to FieldValue.arrayRemove(adoptPostId))
                } else {
                    mapOf("likedPostIds" to FieldValue.arrayUnion(adoptPostId))
                }

                if (!userDocExists) {
                    batch.set(userDocRef, likeMap) // SET nếu là lần đầu tiên
                } else {
                    batch.update(userDocRef, likeMap) // UPDATE nếu đã tồn tại
                }

                batch.update(postDocRef, postUpdateMap)
            }.await()

            Log.d("AdoptRepoImpl", "Toggle Tim cho bài $adoptPostId THÀNH CÔNG")
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("AdoptRepoImpl", "Toggle Tim cho bài $adoptPostId THẤT BẠI", e)
            AuthResult.Error(e.message ?: "Lỗi đéo biết KKK :v")
        }
    }

    override fun getCommentsForAdoptPost(adoptPostId: String): Flow<List<AdoptComment>> {
        return callbackFlow {
            val listener = firestore.collection(ADOPT_COMMENTS_COLLECTION)
                .whereEqualTo("adoptPostId", adoptPostId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("AdoptCommentRepo", "Lỗi nghe bình luận cho $adoptPostId", error)
                        close(error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val comments = snapshot.toObjects(AdoptComment::class.java)
                        trySend(comments)
                    } else {
                        trySend(emptyList())
                    }
                }

            awaitClose {
                listener.remove()
            }
        }
    }

    override suspend fun addComment(comment: AdoptComment): AuthResult<Unit> {
        return try {
            firestore.collection(ADOPT_COMMENTS_COLLECTION).add(comment).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("AdoptCommentRepo", "Thêm bình luận thất bại", e)
            AuthResult.Error(e.message ?: "Lỗi không xác định khi thêm bình luận.")
        }
    }
}