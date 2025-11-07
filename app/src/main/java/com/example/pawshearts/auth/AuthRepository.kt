package com.example.pawshearts.auth

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val auth: Any
    val currentUser: FirebaseUser?

    suspend fun registerWithEmail(email: String, password: String): AuthResult<FirebaseUser>
    suspend fun loginWithEmail(email: String, password: String): AuthResult<FirebaseUser>
    suspend fun signInWithGoogle(idToken: String): AuthResult<FirebaseUser>

    fun logout()
}