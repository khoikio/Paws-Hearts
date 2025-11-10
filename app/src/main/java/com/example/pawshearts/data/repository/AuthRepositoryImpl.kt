package com.example.pawshearts.data.repository

import android.net.Uri
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
import com.google.firebase.auth.FirebaseAuthException // <-- M PHẢI CÓ CÁI NÀY
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlin.toString

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

    override suspend fun loginWithEmail(email: String, password: String): AuthResult<FirebaseUser> {
        return withContext(Dispatchers.IO) {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                AuthResult.Success(result.user!!)

                // CỤC NÀY M CŨNG THIẾU NÈ KKK (CHO LỖI LOGIN)
            } catch (e: FirebaseAuthException) {
                Log.e("AuthRepo", "Login error (Firebase)", e)
                val errorMessage = when (e.errorCode) {
                    "ERROR_NETWORK_REQUEST_FAILED" -> "Lỗi kết nối internet?"
                    "ERROR_INVALID_EMAIL" -> "Email chưa đăng ký"
                    "ERROR_WRONG_PASSWORD" -> "Sai mật khẩu rồi M :("
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

    override suspend fun signInWithGoogle(idToken: String): AuthResult<FirebaseUser> {
        return withContext(Dispatchers.IO) {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                val user = result.user!!
                if (result.additionalUserInfo?.isNewUser == true) {
                    val newUser = UserData(
                        userId = user.uid,
                        username = user.displayName,
                        profilePictureUrl = user.photoUrl?.toString(),
                        email = user.email,
                        phone = null,
                        address = null,
                        role = "user" // <-- T THÊM CẢ VÔ GOOGLE
                    )
                    firestore.collection("users").document(user.uid).set(newUser).await()
                    userDao.insertOrUpdateUser(newUser) // Sync về Room
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
        if (userId != null) {
            withContext(Dispatchers.IO) {
                userDao.deleteUserById(userId)
            }
        }
    }

    // --- CÁC HÀM QUẢN LÝ DỮ LIỆU NGƯỜI DÙNG ---

    override fun getUserProfileFlow(userId: String): Flow<UserData?> {
        if (userId.isBlank()) {
            return flowOf(null)
        }
        firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("AuthRepo", "Lỗi lắng nghe Firestore", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val firestoreUser = snapshot.toObject(UserData::class.java)
                    if (firestoreUser != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            Log.d("AuthRepo", "Syncing data from Firebase to Room for user: ${firestoreUser.userId}")
                            userDao.insertOrUpdateUser(firestoreUser)
                        }
                    }
                }
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
            "username" to newName,
            "email" to newEmail
        )
        // Tạm bỏ qua updateEmail trên Auth (phức tạp)
        firestore.collection("users").document(userId)
            .update(updates)
            .await()
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




