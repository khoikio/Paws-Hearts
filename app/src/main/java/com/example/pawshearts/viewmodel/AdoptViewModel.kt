package com.example.pawshearts.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pawshearts.FakeRepository
import com.example.pawshearts.PetPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AdoptUiState(
    val searchQuery: String = "",
    val allPets: List<PetPost> = emptyList(),
    val displayedPets: List<PetPost> = emptyList()
)

class AdoptViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AdoptUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val allPets = FakeRepository.getFeed().filter { it.status == "open" || it.status == "lost" }
        _uiState.update {
            it.copy(
                allPets = allPets,
                displayedPets = allPets
            )
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
