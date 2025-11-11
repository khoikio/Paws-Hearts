package com.example.pawshearts.activities

import android.util.Log
import com.example.pawshearts.auth.AuthResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.example.pawshearts.data.model.Activity // M nhớ import Activity

// T GIẢ SỬ M SẼ 'inject' CÁI FIREBASE VÔ KKK
class ActivityRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ActivityRepository {

    // === 1. TẠO HÀM LẤY TẤT CẢ ACTIVITIES (NGHE REAL-TIME) KKK ===
    override fun getAllActivitiesFlow(): Flow<List<Activity>> {
        return callbackFlow {
            Log.d("ActivityRepoImpl", "Bắt đầu 'nghe' (listen) TẤT CẢ hoạt động KKK")

            val listener = firestore.collection("activities")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        Log.e("ActivityRepoImpl", "Lỗi nghe Activities KKK", error)
                        close(error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val activities = snapshot.toObjects(Activity::class.java)
                        trySend(activities) // Gửi list TẤT CẢ
                    } else {
                        trySend(emptyList())
                    }
                }

            awaitClose {
                Log.d("ActivityRepoImpl", "Hủy 'nghe' Activities KKK")
                listener.remove()
            }
        }
    }

    // === 2. HÀM TẠO ACTIVITIES (CHO ADMIN) KKK ===
    override suspend fun createActivity(activity: Activity): AuthResult<Unit> {
        return try {
            // T VỚI M SẼ LƯU VÔ COLLECTION "activities" KKK
            firestore.collection("activities").add(activity).await()
            Log.d("ActivityRepoImpl", "Đăng hoạt động THÀNH CÔNG KKK :D")
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("ActivityRepoImpl", "Đăng hoạt động THẤT BẠI KKK :@", e)
            AuthResult.Error(e.message ?: "Lỗi đéo biết KKK :v")
        }
    }
}