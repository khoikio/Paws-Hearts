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
import com.google.firebase.firestore.FieldValue


// T GIẢ SỬ M SẼ 'inject' CÁI FIREBASE VÔ KKK
class ActivityRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ActivityRepository {
    private val activitiesCollection = firestore.collection("activities")

    // === 1. TẠO HÀM LẤY TẤT CẢ ACTIVITIES (NGHE REAL-TIME) KKK ===
    override fun getAllActivitiesFlow(): Flow<List<Activity>> {
        return callbackFlow {
            Log.d("ActivityRepoImpl", "Bắt đầu 'nghe' (listen) TẤT CẢ hoạt động")

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
    override suspend fun getActivityById(activityId: String): Activity? {
        return try {
            // 1. Dùng ID để lấy đúng document từ collection "activities"
            val documentSnapshot = activitiesCollection.document(activityId).get().await()

            // 2. Chuyển đổi document thành đối tượng Activity và trả về
            documentSnapshot.toObject(Activity::class.java)
        } catch (e: Exception) {
            Log.e("ActivityRepoImpl", "Lỗi khi lấy hoạt động theo ID: $activityId", e)
            null // Trả về null nếu có lỗi hoặc không tìm thấy document
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

    override suspend fun deleteActivity(activityId: String) {
        try {
            // Gọi lệnh xóa document có ID tương ứng trên Firestore
            activitiesCollection.document(activityId).delete().await()
            Log.d("ActivityRepo", "Đã xóa hoạt động $activityId thành công.")
        } catch (e: Exception) {
            Log.e("ActivityRepo", "Lỗi khi xóa hoạt động $activityId", e)
            // Ở đây mày có thể ném ra lỗi để báo cho ViewModel biết
        }
    }

    override suspend fun updateActivity(activity: Activity): AuthResult<Unit> {
        return try {
            // Đảm bảo activity có ID hợp lệ để biết cần update document nào
            if (activity.id.isBlank()) {
                throw IllegalArgumentException("Activity ID không được rỗng khi cập nhật")
            }
            // Dùng ID để tìm đúng document và set (ghi đè) dữ liệu mới
            activitiesCollection.document(activity.id).set(activity).await()

            Log.d("ActivityRepoImpl", "Cập nhật hoạt động THÀNH CÔNG")
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("ActivityRepoImpl", "Cập nhật hoạt động THẤT BẠI", e)
            AuthResult.Error(e.message ?: "Lỗi không xác định")
        }
    }
    override suspend fun registerUserToActivity(
        activityId: String,
        userId: String,
        userName: String,
        userAvatar: String
    ): AuthResult<Unit> {
        return try {
            val registrationData = hashMapOf(
                "userId" to userId,
                "userName" to userName,
                "userAvatar" to userAvatar,
                "registeredAt" to FieldValue.serverTimestamp()
            )

            // Lưu vào sub-collection "registrations" bên trong cái activity đó
            firestore.collection("activities").document(activityId)
                .collection("registrations")
                .document(userId) // Dùng userId làm ID document để không bị trùng
                .set(registrationData)
                .await()

            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Lỗi đăng ký")
        }
    }

    override suspend fun checkIsRegistered(activityId: String, userId: String): Boolean {
        return try {
            val doc = firestore.collection("activities").document(activityId)
                .collection("registrations")
                .document(userId)
                .get()
                .await()
            doc.exists() // Trả về true nếu tìm thấy document
        } catch (e: Exception) {
            false
        }
    }
}
