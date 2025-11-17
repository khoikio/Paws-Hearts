package com.example.pawshearts.auth

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import com.example.pawshearts.data.model.UserData

interface AuthRepository {
    val currentUser: FirebaseUser?
    val currentUserStateFlow: Flow<FirebaseUser?>
    val isUserLoggedInFlow: Flow<Boolean>

    suspend fun refreshUserProfile()
    fun getUserProfileFlow(userId: String): Flow<UserData?>
    suspend fun logout()
    suspend fun uploadAvatar(userId: String, uri: Uri): AuthResult<Unit>
    suspend fun updateUserPersonalInfo(phone: String, address: String)
    suspend fun updateProfile(newName: String, newEmail: String)
    suspend fun signInWithGoogle(idToken: String): AuthResult<FirebaseUser>
    suspend fun registerWithEmail(email: String, pass: String, fullName: String): AuthResult<FirebaseUser>
    suspend fun loginWithEmail(email: String, pass: String): AuthResult<FirebaseUser>
}
