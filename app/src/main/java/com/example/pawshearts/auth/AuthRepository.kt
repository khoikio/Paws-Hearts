package com.example.pawshearts.auth

import android.net.Uri
import com.example.pawshearts.data.model.UserData // M nhớ check M import đúng 'UserData' nha
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

/**
 * Interface này định nghĩa các quy tắc cho AuthRepository (CÁI XỊN KKK)
 * Nó giúp cho việc testing và thay đổi nguồn dữ liệu sau này dễ dàng hơn.
 */
interface AuthRepository {
    // === MẤY BIẾN CƠ BẢN ===
    val auth: FirebaseAuth
    val currentUser: FirebaseUser?
    val isUserLoggedInFlow: Flow<Boolean> // <-- Cái này xịn nè

    // === MẤY HÀM ĐĂNG NHẬP/KÝ ===
    suspend fun registerWithEmail(email: String, password: String, fullName: String): AuthResult<FirebaseUser>
    suspend fun loginWithEmail(email: String, password: String): AuthResult<FirebaseUser>
    suspend fun signInWithGoogle(idToken: String): AuthResult<FirebaseUser>
    suspend fun logout() // <-- T thêm suspend cho nó xịn KKK

    // === MẤY HÀM PROFILE ===
    // Hàm lấy thông tin người dùng, trả về một Flow
    fun getUserProfileFlow(userId: String): Flow<UserData?>
    suspend fun refreshUserProfile()

    // Hàm cập nhật thông tin người dùng lên Firebase
    suspend fun updateUserPersonalInfo(phone: String, address: String)
    suspend fun updateProfile(newName: String, newEmail: String)
    suspend fun uploadAvatar(userId: String, uri: Uri): AuthResult<String>

}

// M CHỈ CẦN 1 CÁI INTERFACE NÀY THÔI M ƠI KKK :@
// T XÓA CÁI INTERFACE CÙI BẮP ĐẦU TIÊN CỦA M RỒI KKK :v