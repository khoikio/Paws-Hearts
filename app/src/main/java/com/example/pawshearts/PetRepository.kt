package com.example.pawshearts

class PetRepository {
    fun getAllPets(): List<PetPost> {
        // In a real app, this would fetch data from Firestore
        return emptyList()
    }

    fun getPetById(id: String): PetPost? {
        // In a real app, this would fetch a single document from Firestore
        return null
    }

    fun addPet(pet: PetPost) {
        // In a real app, this would add a new document to Firestore
    }
}
