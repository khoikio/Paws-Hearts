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

class AdoptRepositoryImpl(
    private val firestore: FirebaseFirestore
) : AdoptRepository {

    override fun getMyAdoptPostsFlow(userId: String): Flow<List<Adopt>> {
        return callbackFlow {
            if (userId.isBlank()) {
                Log.w("AdoptRepoImpl", "User ID rỗng, đéo fetch my adopts KKK")
                trySend(emptyList()) // Gửi list rỗng
                close()
                return@callbackFlow
            }

            Log.d("AdoptRepoImpl", "Bắt đầu 'nghe' (listen) bài nhận nuôi của $userId")
            val listener = firestore.collection("adopts") // <-- M LƯU Ở ĐÂU M SỬA Ở ĐÂY
                .whereEqualTo("userId", userId) // <-- M LƯU FIELD TÊN GÌ M SỬA Ở ĐÂY
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        Log.e("AdoptRepoImpl", "Lỗi nghe MyAdopts KKK", error)
                        close(error) // Đóng flow nếu lỗi
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val myAdoptPosts = snapshot.toObjects(Adopt::class.java)
                        Log.d("AdoptRepoImpl", "Nghe thấy ${myAdoptPosts.size} bài KKK. Gửi về VM...")
                        trySend(myAdoptPosts) // Gửi cái list MỚI mỗi khi nó thay đổi KKK
                    } else {
                        Log.d("AdoptRepoImpl", "Snapshot rỗng, chắc đéo có bài nào :v")
                        trySend(emptyList()) // Gửi list rỗng
                    }
                }

            // Khi ViewModel bị hủy (M thoát màn hình), nó tự gỡ listener
            awaitClose {
                Log.d("AdoptRepoImpl", "Hủy 'nghe' MyAdopts KKK")
                listener.remove()
            }
        }
    }
    override fun getAllAdoptPostsFlow(): Flow<List<Adopt>> {
        // T VỚI M "NGHE" REAL-TIME NGUYÊN COLLECTION "adopts" KKK
        return callbackFlow {
            Log.d("AdoptRepoImpl", "Bắt đầu 'nghe' (listen) TẤT CẢ bài nhận nuôi KKK")

            val listener = firestore.collection("adopts")
                .orderBy("timestamp", Query.Direction.DESCENDING) // T VỚI M LẤY MỚI NHẤT
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        Log.e("AdoptRepoImpl", "Lỗi nghe TẤT CẢ Adopts KKK", error)
                        close(error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val allAdoptPosts = snapshot.toObjects(Adopt::class.java)
                        Log.d("AdoptRepoImpl", "Nghe thấy ${allAdoptPosts.size} bài KKK. Gửi về VM...")
                        trySend(allAdoptPosts) // Gửi list TẤT CẢ
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

    override suspend fun createAdoptPost(adoptPost: Adopt): AuthResult<Unit> {
        return try {
            // T VỚI M LƯU VÔ "adopts" KKK
            firestore.collection("adopts").add(adoptPost).await()
            Log.d("AdoptRepoImpl", "Đăng bài nhận nuôi THÀNH CÔNG KKK :D")
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("AdoptRepoImpl", "Đăng bài nhận nuôi THẤT BẠI KKK :@", e)
            AuthResult.Error(e.message ?: "Lỗi đéo biết KKK :v")
        }
    }
}