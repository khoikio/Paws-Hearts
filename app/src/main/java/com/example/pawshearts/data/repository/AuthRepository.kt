package com.example.pawshearts.data.repository


import com.example.pawshearts.auth.AuthResult
import com.example.pawshearts.data.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

/**
 * Interface này định nghĩa các quy tắc cho AuthRepository.
 * Nó giúp cho việc testing và thay đổi nguồn dữ liệu sau này dễ dàng hơn.
 */
interface AuthRepository {
    val auth: FirebaseAuth
    val currentUser: FirebaseUser?
    val isUserLoggedInFlow: Flow<Boolean>
    suspend fun registerWithEmail(email: String, password: String, fullName: String): AuthResult<FirebaseUser>
    suspend fun loginWithEmail(email: String, password: String): AuthResult<FirebaseUser>
    suspend fun signInWithGoogle(idToken: String): AuthResult<FirebaseUser>
    suspend fun logout()

    // Hàm lấy thông tin người dùng, trả về một Flow từ cơ sở dữ liệu cục bộ
    fun getUserProfileFlow(userId: String): Flow<UserData?>

    // Hàm cập nhật thông tin người dùng lên Firebase
    suspend fun updateUserPersonalInfo(phone: String, address: String)
    suspend fun updateProfile(newName: String, newEmail: String)
}
