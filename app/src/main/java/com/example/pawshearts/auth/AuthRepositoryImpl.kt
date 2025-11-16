package com.example.pawshearts.auth

import android.net.Uri
import android.util.Log
import com.example.pawshearts.data.local.UserDao
import com.example.pawshearts.data.model.UserData
import com.example.pawshearts.auth.AuthRepository
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
import com.google.firebase.auth.FirebaseAuthException // <-- M PHẢI CÓ CÁI NÀY
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage

/**
 * Lớp này triển khai (implements) AuthRepository.
 */
class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore,
) : AuthRepository {

    override val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val usersCollection = firestore.collection("users")
    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override val isUserLoggedInFlow: Flow<Boolean>
        get() = auth.authStateChanges().map { user: FirebaseUser? -> user != null }

    // --- CÁC HÀM XÁC THỰC ---

    override suspend fun registerWithEmail(email: String, password: String, fullName: String): AuthResult<FirebaseUser> {
        return withContext(Dispatchers.IO) {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName.trim()) // <-- Lấy fullName
                        .build()
                    user.updateProfile(profileUpdates).await()
                    Log.d("AuthRepo", "Đã cập nhật displayName cho Auth Profile thành công!")
                    val newUser = UserData(
                        userId = user.uid,
                        username = fullName,
                        profilePictureUrl = user.photoUrl?.toString(),
                        email = user.email,
                        phone = null,
                        address = null,
                        role = "user" // <-- T THÊM CÂI 'role' M QUÊN NÈ
                    )
                    firestore.collection("users").document(user.uid).set(newUser).await()
                    userDao.insertOrUpdateUser(newUser)
                    AuthResult.Success(user)
                } else {
                    AuthResult.Error("Đăng ký thất bại, không có thông tin người dùng.")
                }

                // CỤC M BỊ THIẾU NÈ KKK
            } catch (e: FirebaseAuthException) {
                Log.e("AuthRepo", "Register error (Firebase)", e)
                val errorMessage = when (e.errorCode) {
                    "ERROR_NETWORK_REQUEST_FAILED" -> "Lỗi kết nối internet? "
                    "ERROR_INVALID_EMAIL" -> "Email người dùng chưa đăng ký"
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "Email đã đăng ký" // LỖI CỦA M NÈ
                    "ERROR_WEAK_PASSWORD" -> "Mật khẩu ít nhất 6 ký tự"
                    else -> "Lỗi đăng ký: ${e.message}"
                }
                AuthResult.Error(errorMessage)

            } catch (e: Exception) {
                Log.e("AuthRepo", "Register error (Non-Firebase)", e)
                AuthResult.Error(e.message ?: "Lỗi -.-")
            }
        }
    }
    private suspend fun syncProfileToLocal(userId: String) {
        try {
            val documentSnapshot = firestore.collection("users").document(userId).get().await()
            val firestoreUser = documentSnapshot.toObject(UserData::class.java)
            if (firestoreUser != null) {
                userDao.insertOrUpdateUser(firestoreUser)
                Log.d("AuthRepo", "Đã đồng bộ profile từ Firestore về Room cho user: $userId")
            } else {
                Log.w("AuthRepo", "Không tìm thấy user trên Firestore để đồng bộ: $userId")
            }
        } catch (e: Exception) {
            Log.e("AuthRepo", "Lỗi khi đồng bộ profile về Room", e)
        }
    }
    override suspend fun loginWithEmail(email: String, password: String): AuthResult<FirebaseUser> {
        return withContext(Dispatchers.IO) {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user!!
                // GỌI HÀM ĐỒNG BỘ NGAY SAU KHI ĐĂNG NHẬP THÀNH CÔNG
                syncProfileToLocal(user.uid)
                AuthResult.Success(user)
            } catch (e: FirebaseAuthException) {
                Log.e("AuthRepo", "Login error (Firebase)", e)
                val errorMessage = when (e.errorCode) {
                    "ERROR_NETWORK_REQUEST_FAILED" -> "Lỗi kết nối internet?"
                    "ERROR_INVALID_EMAIL" -> "Email chưa đăng ký"
                    "ERROR_WRONG_PASSWORD" -> "Sai mật khẩu rồi:("
                    "ERROR_USER_NOT_FOUND" -> "Email chưa đăng ký"
                    "ERROR_INVALID_CREDENTIAL" -> "Sai email hoặc mật khẩu "
                    else -> "Lỗi đéo biết: ${e.message}"
                }
                AuthResult.Error(errorMessage)
            } catch (e: Exception) {
                Log.e("AuthRepo", "Login error (Non-Firebase)", e)
                AuthResult.Error(e.message ?: "Lỗi -.-")
            }
        }
    }

    // TRONG FILE AuthRepositoryImpl.kt

    override suspend fun signInWithGoogle(idToken: String): AuthResult<FirebaseUser> {
        return withContext(Dispatchers.IO) {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                val user = result.user!!

                // --- LOGIC "UPSERT" - KIỂM TRA VÀ TẠO MỚI ---
                val userDocRef = firestore.collection("users").document(user.uid)
                val document = userDocRef.get().await()

                // NẾU DOCUMENT CHƯA TỒN TẠI
                if (!document.exists()) {
                    Log.w("AuthRepo", "User ${user.uid} chưa có document trên Firestore, đang tạo mới...")
                    val newUser = UserData(
                        userId = user.uid,
                        username = user.displayName,
                        profilePictureUrl = user.photoUrl?.toString(),
                        email = user.email,
                        phone = null,
                        address = null,
                        role = "user",
                        isAdmin = false // Thêm cả trường mới vào
                    )
                    // TẠO MỚI DOCUMENT
                    userDocRef.set(newUser).await()
                }
                // --- KẾT THÚC LOGIC "UPSERT" ---

                // Bây giờ ta chắc chắn document đã tồn tại, tiến hành đồng bộ về Room
                syncProfileToLocal(user.uid)

                AuthResult.Success(user)
            } catch (e: Exception) {
                Log.e("AuthRepository", "Google sign-in error: ${e.message}", e)
                AuthResult.Error(e.message ?: "Lỗi đăng nhập với Google.")
            }
        }
    }

    override suspend fun refreshUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        withContext(Dispatchers.IO) {
            syncProfileToLocal(userId) // Gọi lại hàm private cho gọn
        }
    }

    override suspend fun logout() {
        val userId = currentUser?.uid // Lấy ID của user hiện tại

        // BƯỚC 1: XÓA DỮ LIỆU CỦA USER NÀY KHỎI ROOM TRƯỚC
        if (userId != null) {
            withContext(Dispatchers.IO) {
                Log.d("AuthRepo", "Đang xóa user $userId khỏi Room...")
                userDao.deleteUserById(userId)
            }
        }

        // BƯỚC 2: SAU KHI DỌN DẸP XONG, MỚI ĐĂNG XUẤT KHỎI FIREBASE
        auth.signOut()
        Log.d("AuthRepo", "Đã đăng xuất khỏi Firebase.")
    }

    // --- CÁC HÀM QUẢN LÝ DỮ LIỆU NGƯỜI DÙNG ---

    override fun getUserProfileFlow(userId: String): Flow<UserData?> {
        if (userId.isBlank()) {
            return flowOf(null)
        }
        return userDao.getUserById(userId)
    }

    override suspend fun uploadAvatar(userId: String, uri: Uri): AuthResult<String> {
        return try {
            val fileName = "avatars/${userId}_${System.currentTimeMillis()}"
            val imageRef = storage.reference.child(fileName)

            // 2. ĐẨY FILE LÊN (putFile)
            imageRef.putFile(uri).await()

            // 3. LẤY LẠI CÁI LINK WEB (http://...)
            val downloadUrl = imageRef.downloadUrl.await()
            val urlString = downloadUrl.toString()

            // 4. CẬP NHẬT LẠI FIRESTORE (Database)
            // (Nó tự báo 'likes' / 'commentCount' vì M 'Gỡ App' rồi KKK)
            usersCollection.document(userId).update("profilePictureUrl", urlString).await()

            Log.d("AuthRepoImpl", "Up avatar thành công: $urlString")
            AuthResult.Success(urlString) // <-- Trả link xịn

        } catch (e: Exception) {
            Log.e("AuthRepoImpl", "Lỗi up avatar", e)
            AuthResult.Error(e.message ?: "Lỗi cmnr")
        }
    }

    override suspend fun updateUserPersonalInfo(phone: String, address: String) {
        val userId = auth.currentUser?.uid ?: return
        withContext(Dispatchers.IO) { // Chạy trên luồng IO
            try {
                val updates = mapOf(
                    "phone" to phone,
                    "address" to address
                )
                // 1. Cập nhật lên Firestore
                firestore.collection("users").document(userId).update(updates).await()

                // 2. ĐỒNG BỘ LẠI VỀ ROOM NGAY LẬP TỨC
                syncProfileToLocal(userId)
                Log.d("AuthRepo", "Đã cập nhật và đồng bộ SĐT, Địa chỉ.")
            } catch (e: Exception) {
                Log.e("AuthRepo", "Lỗi khi cập nhật thông tin cá nhân", e)
            }
        }
    }

    override suspend fun updateProfile(newName: String, newEmail: String) {
        val userId = auth.currentUser?.uid ?: return
        withContext(Dispatchers.IO) {
            try {
                val updates = mapOf(
                    "username" to newName,
                    "email" to newEmail
                )
                // 1. Cập nhật lên Firestore
                firestore.collection("users").document(userId).update(updates).await()

                // 2. ĐỒNG BỘ LẠI VỀ ROOM NGAY LẬP TỨC
                syncProfileToLocal(userId)
                Log.d("AuthRepo", "Đã cập nhật và đồng bộ Tên, Email.")
            } catch (e: Exception) {
                Log.e("AuthRepo", "Lỗi khi cập nhật profile", e)
            }
        }
    }
}
private fun FirebaseAuth.authStateChanges(): Flow<FirebaseUser?> {
    return callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        addAuthStateListener(listener)
        awaitClose {
            removeAuthStateListener(listener)
        }
    }

}




