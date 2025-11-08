package com.example.pawshearts

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class PetRepository {

    private val db: FirebaseFirestore = Firebase.firestore
    private val petsCollection = db.collection("pets")

    // Lấy tất cả các bài đăng thú cưng từ Firestore
    suspend fun getAllPets(): Result<List<PetPost>> {
        return try {
            val snapshot = petsCollection.get().await()
            val pets = snapshot.documents.mapNotNull { document ->
                // Sử dụng toObject để ánh xạ trực tiếp từ Firestore Document sang PetPost
                document.toObject(PetPost::class.java)?.copy(postId = document.id)
            }
            Result.success(pets)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Lấy một bài đăng thú cưng theo ID từ Firestore
    suspend fun getPetById(id: String): Result<PetPost?> {
        return try {
            val document = petsCollection.document(id).get().await()
            val pet = document.toObject(PetPost::class.java)?.copy(postId = document.id)
            Result.success(pet)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Thêm một bài đăng thú cưng mới vào Firestore
    suspend fun addPet(pet: PetPost): Result<Boolean> {
        return try {
            petsCollection.add(pet).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // TODO: Implement update and delete functions as needed
}
