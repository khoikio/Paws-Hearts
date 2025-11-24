package com.example.pawshearts.auth

import android.net.Uri
import android.util.Log
import com.example.pawshearts.data.local.UserDao
import com.example.pawshearts.data.model.UserData
import com.example.pawshearts.image.CloudinaryService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao,
    private val cloudinaryService: CloudinaryService
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override val currentUserStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override val isUserLoggedInFlow: Flow<Boolean>
        get() = userDao.getLoggedInUserFlow().map { it != null }

    override suspend fun refreshUserProfile() = withContext(Dispatchers.IO) {
        val userId = currentUser?.uid ?: return@withContext
        fetchAndCacheUserProfile(userId) // Gọi hàm chung cho gọn
    }
    
    // HÀM MỚI ĐỂ LẤY DATA CỦA BẤT KỲ AI
    override suspend fun fetchAndCacheUserProfile(userId: String) = withContext(Dispatchers.IO) {
        if (userId.isEmpty()) return@withContext
        try {
            val snapshot = firestore.collection("users").document(userId).get().await()
            val userData = snapshot.toObject(UserData::class.java)
            if (userData != null) {
                userDao.insertUser(userData)
                Log.d("AuthRepo", "Đã đồng bộ profile từ Firestore về Room cho user: $userId")
            }
        } catch (e: Exception) {
            Log.e("AuthRepo", "Lỗi khi đồng bộ profile của user $userId", e)
        }
    }

    override fun getUserProfileFlow(userId: String): Flow<UserData?> {
        return userDao.getUserByIdFlow(userId)
    }

    override suspend fun logout() {
        withContext(Dispatchers.IO) {
            val userId = currentUser?.uid
            auth.signOut()
            if (userId != null) {
                userDao.deleteUserById(userId)
            }
        }
    }

    override suspend fun uploadImage(imageFile: File): AuthResult<String> {
        return try {
            val presetName = "paws-hearts"
            val presetBody = RequestBody.create("text/plain".toMediaTypeOrNull(), presetName)
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
            val filePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val response = cloudinaryService.uploadImage(filePart, presetBody)

            if (response.secure_url != null) {
                AuthResult.Success(response.secure_url)
            } else {
                AuthResult.Error("Không nhận được link ảnh")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AuthResult.Error("Lỗi upload: ${e.message}")
        }
    }
    override suspend fun updateUserAvatar(userId: String, newUrl: String): AuthResult<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .update("profilePictureUrl", newUrl) // Đảm bảo tên trường trong DB đúng là 'profilePictureUrl'
                .await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error("Lỗi lưu DB: ${e.message}")
        }
    }
    override suspend fun updateUserPersonalInfo(phone: String, address: String) = withContext(Dispatchers.IO) {
        val userId = currentUser?.uid ?: return@withContext
        try {
            val updates = mapOf("phone" to phone, "address" to address)
            firestore.collection("users").document(userId).update(updates).await()
            refreshUserProfile()
        } catch (e: Exception) {
            // Xử lý lỗi
        }
    }

    override suspend fun updateProfile(newName: String, newEmail: String) = withContext(Dispatchers.IO) {
        val userId = currentUser?.uid ?: return@withContext
        try {
            val updates = mapOf("username" to newName, "email" to newEmail)
            firestore.collection("users").document(userId).update(updates).await()
            refreshUserProfile()
        } catch (e: Exception) {
            // Xử lý lỗi
        }
    }

    override suspend fun signInWithGoogle(idToken: String): AuthResult<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user!!
            
            val userDoc = firestore.collection("users").document(user.uid).get().await()
            if (!userDoc.exists()) {
                val newUser = UserData(
                    userId = user.uid,
                    username = user.displayName,
                    email = user.email,
                    profilePictureUrl = user.photoUrl?.toString()
                )
                firestore.collection("users").document(user.uid).set(newUser).await()
            }
            
            refreshUserProfile()
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Lỗi không xác định")
        }
    }

    override suspend fun registerWithEmail(email: String, pass: String, fullName: String): AuthResult<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
            val user = authResult.user!!
            val newUser = UserData(
                userId = user.uid,
                username = fullName,
                email = email
            )
            firestore.collection("users").document(user.uid).set(newUser).await()
            refreshUserProfile()
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Lỗi không xác định")
        }
    }

    override suspend fun loginWithEmail(email: String, pass: String): AuthResult<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val authResult = auth.signInWithEmailAndPassword(email, pass).await()
            refreshUserProfile()
            AuthResult.Success(authResult.user!!)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Đăng nhập thất bại, vui lòng kiểm tra lại thông tin.")
        }
    }

    override suspend fun toggleFollow(targetUserId: String): AuthResult<Unit> =
        withContext(Dispatchers.IO) {
            val me = auth.currentUser ?: return@withContext AuthResult.Error("Chưa đăng nhập")
            val meId = me.uid

            try {
                val targetRef = firestore.collection("users").document(targetUserId)
                val meRef = firestore.collection("users").document(meId)

                firestore.runTransaction { tx ->
                    val targetSnap = tx.get(targetRef)
                    val meSnap = tx.get(meRef)

                    val targetFollowers = targetSnap.get("followers") as? List<String> ?: emptyList()
                    val meFollowing = meSnap.get("following") as? List<String> ?: emptyList()

                    val isFollowing = meId in targetFollowers

                    val newTargetFollowers =
                        if (isFollowing) targetFollowers - meId
                        else targetFollowers + meId

                    val newMeFollowing =
                        if (isFollowing) meFollowing - targetUserId
                        else meFollowing + targetUserId

                    tx.update(targetRef, "followers", newTargetFollowers)
                    tx.update(meRef, "following", newMeFollowing)
                }.await()

                AuthResult.Success(Unit)
            } catch (e: Exception) {
                AuthResult.Error(e.message ?: "Lỗi không xác định")
            }
        }
}
