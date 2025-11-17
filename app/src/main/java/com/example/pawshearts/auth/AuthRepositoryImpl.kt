package com.example.pawshearts.auth

import android.net.Uri
import android.util.Log
import com.example.pawshearts.data.local.UserDao
import com.example.pawshearts.data.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao,
    private val storage: FirebaseStorage
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

    override suspend fun uploadAvatar(userId: String, uri: Uri): AuthResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val storageRef = storage.reference.child("avatars/$userId")
            val downloadUrl = storageRef.putFile(uri).await().storage.downloadUrl.await().toString()
            firestore.collection("users").document(userId).update("profilePictureUrl", downloadUrl).await()
            refreshUserProfile()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Lỗi không xác định")
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

    override suspend fun toggleFollow(targetUserId: String): AuthResult<Unit> = withContext(Dispatchers.IO) {
        val currentUser = auth.currentUser ?: return@withContext AuthResult.Error("Chưa đăng nhập")
        return@withContext try {
            val action = hashMapOf(
                "actorId" to currentUser.uid,
                "targetId" to targetUserId,
                "type" to "TOGGLE_FOLLOW",
                "timestamp" to FieldValue.serverTimestamp()
            )
            firestore.collection("pending_actions").add(action).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Lỗi không xác định")
        }
    }
}
