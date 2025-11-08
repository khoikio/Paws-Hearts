package com.example.pawshearts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawshearts.PetPost
import com.example.pawshearts.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val searchQuery: String = "",
    val allPosts: List<PetPost> = emptyList(),
    val displayedPosts: List<PetPost> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val petRepository = PetRepository()

    init {
        loadPosts()
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = petRepository.getAllPets()
            result.onSuccess { posts ->
                _uiState.update { currentState ->
                    currentState.copy(allPosts = posts, displayedPosts = posts, isLoading = false)
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to load posts") }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterPosts(query)
    }

    private fun filterPosts(query: String) {
        val filteredList = if (query.isBlank()) {
            _uiState.value.allPosts
        } else {
            _uiState.value.allPosts.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.location.contains(query, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(displayedPosts = filteredList) }
    }
}
