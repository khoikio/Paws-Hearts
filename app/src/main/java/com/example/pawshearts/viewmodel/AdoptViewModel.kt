package com.example.pawshearts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawshearts.PetPost
import com.example.pawshearts.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdoptUiState(
    val searchQuery: String = "",
    val allPets: List<PetPost> = emptyList(),
    val displayedPets: List<PetPost> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class AdoptViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AdoptUiState())
    val uiState = _uiState.asStateFlow()

    private val petRepository = PetRepository()

    init {
        loadPets()
    }

    private fun loadPets() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = petRepository.getAllPets()
            result.onSuccess { allPosts ->
                val petsToAdopt = allPosts.filter { post -> post.status == "open" || post.status == "lost" }
                _uiState.update { currentState ->
                    currentState.copy(
                        allPets = petsToAdopt,
                        displayedPets = petsToAdopt,
                        isLoading = false
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to load pets for adoption") }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterPets(query)
    }

    private fun filterPets(query: String) {
        val filteredList = if (query.isBlank()) {
            _uiState.value.allPets
        } else {
            _uiState.value.allPets.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.location.contains(query, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(displayedPets = filteredList) }
    }
}
