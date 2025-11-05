package com.example.secondaryflow.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PetViewModel : ViewModel() {
    private val repository = PetRepository()

    private val _pets = MutableStateFlow<List<PetPost>>(emptyList())
    val pets: StateFlow<List<PetPost>> = _pets

    fun loadPets() {
        viewModelScope.launch {
            _pets.value = repository.getAllPets()
        }
    }

    fun addSamplePet() {
        viewModelScope.launch {
            repository.addPet(
                PetPost(
                    name = "Buddy",
                    age = 2,
                    breed = "Golden Retriever",
                    imageUrl = "https://i.imgur.com/7lZbA.jpg",
                    description = "Rất thân thiện và ngoan.",
                    location = "Hà Nội"
                )
            )
        }
    }
}
