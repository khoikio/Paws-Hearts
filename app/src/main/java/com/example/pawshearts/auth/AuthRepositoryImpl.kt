package com.example.pawshearts.auth

import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepositoryImpl : AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    // 1. Đăng ký Email/Password
    override suspend fun registerWithEmail(email: String, password: String): AuthResult<FirebaseUser> {
        return withContext(Dispatchers.IO) {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null) {
                    AuthResult.Success(user)
                } else {
                    AuthResult.Error("Đăng ký thất bại — không nhận được user từ server.")
                }
            } catch (e: FirebaseAuthWeakPasswordException) {
                Log.e("AuthRepo", "WeakPassword", e)
                AuthResult.Error("Mật khẩu quá yếu. Hãy chọn mật khẩu dài hơn 6 ký tự.")
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                Log.e("AuthRepo", "InvalidCredentials", e)
                AuthResult.Error("Email không hợp lệ.")
            } catch (e: FirebaseAuthUserCollisionException) {
                Log.e("AuthRepo", "UserCollision", e)
                AuthResult.Error("Tài khoản đã tồn tại với email này.")
            } catch (e: FirebaseNetworkException) {
                Log.e("AuthRepo", "NetworkError", e)
                AuthResult.Error("Lỗi mạng. Vui lòng kiểm tra kết nối Internet và thử lại.")
            } catch (e: Exception) {
                Log.e("AuthRepo", "Register error", e)
                AuthResult.Error(e.message ?: "Lỗi khi đăng ký. Vui lòng thử lại.")
            }
        }
    }

    // 2. Đăng nhập Email/Password
    override suspend fun loginWithEmail(email: String, password: String): AuthResult<FirebaseUser> {
        return withContext(Dispatchers.IO) {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null) {
                    AuthResult.Success(user)
                } else {
                    AuthResult.Error("Đăng nhập thất bại")
                }
            } catch (e: Exception) {
                AuthResult.Error(e.message ?: "Email hoặc mật khẩu không đúng")
            }
        }
    }

    // 3. Đăng nhập Google
    override suspend fun signInWithGoogle(idToken: String): AuthResult<FirebaseUser> {
        return withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("AuthRepository", "Creating credential...")
                val credential = GoogleAuthProvider.getCredential(idToken, null)

                android.util.Log.d("AuthRepository", "Signing in with credential...")
                val result = auth.signInWithCredential(credential).await()

                val user = result.user
                android.util.Log.d("AuthRepository", "User: ${user?.email}")

                if (user != null) {
                    android.util.Log.d("AuthRepository", "Success!")
                    AuthResult.Success(user)
                } else {
                    android.util.Log.e("AuthRepository", "User is null")
                    AuthResult.Error("Đăng nhập Google thất bại")
                }
            } catch (e: Exception) {
                android.util.Log.e("AuthRepository", "Error: ${e.message}", e)
                AuthResult.Error(e.message ?: "Lỗi đăng nhập Google")
            }
        }
    }

    // 4. Đăng xuất
    override fun logout() {
        auth.signOut()
    }
}