package com.example.pawshearts.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pawshearts.FakeRepository
import com.example.pawshearts.PetPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HomeUiState(
    val searchQuery: String = "",
    val allPosts: List<PetPost> = emptyList(),
    val displayedPosts: List<PetPost> = emptyList()
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val allPosts = FakeRepository.getFeed()
        _uiState.update {
            it.copy(
                allPosts = allPosts,
                displayedPosts = allPosts
            )
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
