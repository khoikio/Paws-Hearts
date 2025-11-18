package com.example.pawshearts.adopt

import android.util.Log
import com.example.pawshearts.auth.AuthResult
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AdoptRepositoryImpl(
    private val firestore: FirebaseFirestore
) : AdoptRepository {
    private val ADOPTS_COLLECTION = "adopt_posts"

    override fun getMyAdoptPostsFlow(userId: String): Flow<List<Adopt>> {
        return callbackFlow {
            if (userId.isBlank()) {
                trySend(emptyList())
                close()
                return@callbackFlow
            }

            val listener = firestore.collection(ADOPTS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("AdoptRepoImpl", "Lỗi nghe MyAdopts", error)
                        close(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        trySend(snapshot.toObjects(Adopt::class.java))
                    } else {
                        trySend(emptyList())
                    }
                }
            awaitClose { listener.remove() }
        }
    }

    override fun getAllAdoptPostsFlow(
        species: String?,
        minAge: Int?,
        maxAge: Int?,
        location: String?
    ): Flow<List<Adopt>> {
        return callbackFlow {
            var query: Query = firestore.collection(ADOPTS_COLLECTION)

            if (!species.isNullOrBlank() && species != "Tất cả") {
                query = query.whereEqualTo("petBreed", species)
            }
            if (minAge != null && minAge > 0) {
                query = query.whereGreaterThanOrEqualTo("petAge", minAge)
            }
            if (maxAge != null && maxAge > 0) {
                query = query.whereLessThanOrEqualTo("petAge", maxAge)
            }
            if (!location.isNullOrBlank() && location != "Tất cả") {
                query = query.whereEqualTo("petLocation", location)
            }

            query = query.orderBy("createdAt", Query.Direction.DESCENDING)

            val listener = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AdoptRepoImpl", "Lỗi nghe AllAdopts với Filter", error)
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.toObjects(Adopt::class.java))
                } else {
                    trySend(emptyList())
                }
            }
            awaitClose { listener.remove() }
        }
    }

    override fun getNewAdoptPostId(): String {
        return firestore.collection(ADOPTS_COLLECTION).document().id
    }

    override suspend fun createAdoptPostWithId(id: String, adoptPost: Adopt): AuthResult<Unit> {
        return try {
            val finalPost = adoptPost.copy(createdAt = Timestamp.now())
            firestore.collection(ADOPTS_COLLECTION).document(id).set(finalPost).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Lỗi không xác định")
        }
    }

    override suspend fun getAdoptPostById(postId: String): Adopt? {
        return try {
            firestore.collection(ADOPTS_COLLECTION).document(postId).get().await()
                .toObject(Adopt::class.java)
        } catch (e: Exception) {
            null
        }
    }
}