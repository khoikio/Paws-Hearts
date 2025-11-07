package com.example.pawshearts.data.repository

import android.util.Log
import com.example.pawshearts.auth.AuthResult
import com.example.pawshearts.data.local.UserDao
import com.example.pawshearts.data.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Lớp này triển khai (implements) AuthRepository.
 * Đây là "người trung gian" thực sự, chịu trách nhiệm:
 * 1. Giao tiếp với Firebase (Authentication và Firestore).
 * 2. Giao tiếp với cơ sở dữ liệu Room cục bộ (thông qua UserDao).
 * 3. Đồng bộ hóa dữ liệu giữa Firebase và Room.
 */
class AuthRepositoryImpl(
    private val userDao: UserDao,          // Công cụ để nói chuyện với Room
    private val firestore: FirebaseFirestore,  // Công cụ để nói chuyện với Firestore
) : AuthRepository {
    // Lấy instance của Firebase Auth để sử dụng trong toàn bộ lớp
    override val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Cung cấp một cách nhanh để lấy người dùng hiện tại
    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    /**
     * Hàm này M gọi ở trên nhưng M chưa code (M xóa TODO á KKK)
     * Nó sẽ trả về một Flow<Boolean> báo cáo trạng thái đăng nhập
     */
    override val isUserLoggedInFlow: Flow<Boolean>
        get() = auth.authStateChanges().map { user: FirebaseUser? -> user != null }


    // --- CÁC HÀM XÁC THỰC ---

    override suspend fun registerWithEmail(email: String, password: String, fullName: String): AuthResult<FirebaseUser> {
        return withContext(Dispatchers.IO) { // Chạy trên luồng nền để không treo UI
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null) {
                    // 1. TẠO BẢN GHI BAN ĐẦU TRÊN FIRESTORE cho người dùng mới
                    val newUser = UserData(
                        userId = user.uid,
                        username = fullName,
                        profilePictureUrl = user.photoUrl?.toString(),
                        email = user.email,
                        phone = null, // Các trường mới ban đầu là null
                        address = null
                    )
                    firestore.collection("users").document(user.uid).set(newUser).await()

                    // 2. (Tùy chọn nhưng nên có) Ghi ngay vào Room để UI cập nhật tức thì
                    userDao.insertOrUpdateUser(newUser)

                    AuthResult.Success(user)
                } else {
                    AuthResult.Error("Đăng ký thất bại, không có thông tin người dùng.")
                }
            } catch (e: Exception) {
                // Giữ nguyên các khối catch lỗi của bạn để xử lý các trường hợp cụ thể
                Log.e("AuthRepo", "Register error", e)
                AuthResult.Error(e.message ?: "Lỗi không xác định khi đăng ký.")
            }
        }
    }

    override suspend fun loginWithEmail(email: String, password: String): AuthResult<FirebaseUser> {
        return withContext(Dispatchers.IO) {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                // Sau khi đăng nhập, listener trong `getUserProfileFlow` sẽ tự động lo việc
                // đồng bộ dữ liệu từ Firebase về Room.
                AuthResult.Success(result.user!!)
            } catch (e: Exception) {
                Log.e("AuthRepo", "Login error", e)
                AuthResult.Error(e.message ?: "Lỗi không xác định khi đăng nhập.")
            }
        }
    }

    override suspend fun signInWithGoogle(idToken: String): AuthResult<FirebaseUser> {
        return withContext(Dispatchers.IO) {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                val user = result.user!!

                // Nếu là người dùng mới, tạo bản ghi trên Firestore
                if (result.additionalUserInfo?.isNewUser == true) {
                    val newUser = UserData(
                        userId = user.uid,
                        username = user.displayName,
                        profilePictureUrl = user.photoUrl?.toString(),
                        email = user.email,
                        phone = null,
                        address = null
                    )
                    firestore.collection("users").document(user.uid).set(newUser).await()
                }

                AuthResult.Success(user)
            } catch (e: Exception) {
                Log.e("AuthRepository", "Google sign-in error: ${e.message}", e)
                AuthResult.Error(e.message ?: "Lỗi đăng nhập với Google.")
            }
        }
    }

    override suspend fun logout() {
        val userId = currentUser?.uid
        auth.signOut()
        // Xóa dữ liệu người dùng khỏi Room cục bộ để bảo mật
        if (userId != null) {
            withContext(Dispatchers.IO) { // Đảm bảo chạy trên IO
                userDao.deleteUserById(userId)
            }
        }
    }

    // --- CÁC HÀM QUẢN LÝ DỮ LIỆU NGƯỜI DÙNG ---

    override fun getUserProfileFlow(userId: String): Flow<UserData?> {
        // Nếu không có userId, trả về một Flow rỗng để tránh lỗi
        if (userId.isBlank()) {
            return flowOf(null)
        }

        // Mở một kênh lắng nghe thời gian thực với tài liệu của người dùng trên Firestore
        firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("AuthRepo", "Lỗi lắng nghe Firestore", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    // Chuyển đổi tài liệu Firestore thành đối tượng UserData
                    val firestoreUser = snapshot.toObject(UserData::class.java)
                    if (firestoreUser != null) {
                        // Khi có dữ liệu mới từ Firebase, cập nhật nó vào Room
                        // Việc này chạy trên một coroutine riêng để không block luồng chính
                        CoroutineScope(Dispatchers.IO).launch {
                            Log.d("AuthRepo", "Syncing data from Firebase to Room for user: ${firestoreUser.userId}")
                            userDao.insertOrUpdateUser(firestoreUser)
                        }
                    }
                }
            }

        return userDao.getUserById(userId)
    }

    override suspend fun updateUserPersonalInfo(phone: String, address: String) {
        val userId = auth.currentUser?.uid ?: return

        val updates = mapOf(
            "phone" to phone,
            "address" to address
        )

        firestore.collection("users").document(userId)
            .update(updates)
            .await()
    }


    override suspend fun updateProfile(newName: String, newEmail: String) {
        val userId = auth.currentUser?.uid ?: return

        val updates = mapOf(
            "username" to newName, // Cập nhật username (M đặt là username trong UserData)
            "email" to newEmail
        )

        // 1. Cập nhật Auth (Tạm thời T bỏ qua, vì đổi email Auth phức tạp)
        // auth.currentUser?.updateEmail(newEmail)?.await()

        // 2. Cập nhật Firestore
        firestore.collection("users").document(userId)
            .update(updates)
            .await()

        // Listener trong 'getUserProfileFlow' sẽ tự thấy và cập nhật Room
    }
}


/**
 * HÀM M BỊ THIẾU NÈ KKK :v
 * Hàm này T code để M dùng cho cái 'isUserLoggedInFlow' ở trên
 */
private fun FirebaseAuth.authStateChanges(): Flow<FirebaseUser?> {
    return callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            // Gửi user (có thể null) vào Flow
            trySend(auth.currentUser)
        }
        // Gắn listener vào
        addAuthStateListener(listener)

        // Khi Flow bị hủy (VD: ViewModel bị destroy), gỡ listener ra
        awaitClose {
            removeAuthStateListener(listener)
        }
    }
}

