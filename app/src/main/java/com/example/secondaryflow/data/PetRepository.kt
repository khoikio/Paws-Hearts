package com.example.secondaryflow.data


import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class PetRepository {
    private val db = FirebaseFirestore.getInstance()
    private val petsCollection = db.collection("pets")

    suspend fun getAllPets(): List<PetPost> {
        return try {
            petsCollection.get().await().map { doc ->
                doc.toObject(PetPost::class.java).copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPetById(id: String): PetPost? {
        return try {
            val doc = petsCollection.document(id).get().await()
            if (doc.exists()) doc.toObject(PetPost::class.java)?.copy(id = doc.id)
            else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addPet(pet: PetPost): Boolean {
        return try {
            val newDoc = petsCollection.document()
            newDoc.set(pet.copy(id = newDoc.id)).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
