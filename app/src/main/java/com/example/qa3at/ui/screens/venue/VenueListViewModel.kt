package com.example.qa3at.ui.screens.venue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qa3at.data.repository.Result
import com.example.qa3at.data.repository.VenueRepository
import com.example.qa3at.domain.model.Venue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VenueListUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val allVenues: List<Venue> = emptyList(),
    val displayedVenues: List<Venue> = emptyList(),
    val filteredVenues: List<Venue> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "all", // "all", "hotel", "hall"
    val selectedCity: String = "all",
    val selectedSort: String = "default",
    val cities: List<String> = emptyList(),
    val hasMore: Boolean = false,
    val currentPage: Int = 1,
    val error: String? = null
) {
    companion object {
        const val PAGE_SIZE = 6
    }
}

@HiltViewModel
class VenueListViewModel @Inject constructor(
    private val venueRepository: VenueRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VenueListUiState(isLoading = true))
    val uiState: StateFlow<VenueListUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadVenues()
    }

    fun loadVenues() {
        searchVenues(reset = true)
        loadCities()
    }

    private fun loadCities() {
        viewModelScope.launch {
            val result = venueRepository.getCities()
            if (result is Result.Success) {
                // Mapping cities to strings as UI expects strings
                val cityNames = result.data.map { it.name }.distinct().sorted()
                _uiState.update { it.copy(cities = cityNames) }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchVenues(reset = true)
    }

    fun updateCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        searchVenues(reset = true)
    }

    fun updateCity(city: String) {
        _uiState.update { it.copy(selectedCity = city) }
        searchVenues(reset = true)
    }

    fun updateSort(sort: String) {
        _uiState.update { it.copy(selectedSort = sort) }
        searchVenues(reset = true)
    }

    fun loadMore() {
        if (_uiState.value.isLoading || _uiState.value.isLoadingMore || !_uiState.value.hasMore) return
        searchVenues(reset = false)
    }

    private fun searchVenues(reset: Boolean) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val currentState = _uiState.value
            val page = if (reset) 1 else currentState.currentPage + 1
            
            if (reset) {
                _uiState.update { it.copy(isLoading = true, displayedVenues = emptyList(), filteredVenues = emptyList(), hasMore = true, currentPage = 1, error = null) }
            } else {
                _uiState.update { it.copy(isLoadingMore = true, error = null) }
            }

            // Map parameters
            val cityParam = if (currentState.selectedCity != "all") currentState.selectedCity else null
            val typeParam = when (currentState.selectedCategory) {
                "hotel" -> "HOTEL"
                "hall" -> "HALL"
                else -> null
            }
            val queryParam = currentState.searchQuery.takeIf { it.isNotBlank() }
            val sortByParam = if (currentState.selectedSort != "default") currentState.selectedSort else null

            val result = venueRepository.searchVenues(
                query = queryParam,
                city = cityParam,
                type = typeParam,
                sortBy = sortByParam,
                page = page,
                limit = 6
            )

            when (result) {
                is Result.Success -> {
                    val (newVenues, totalPages) = result.data
                    _uiState.update { 
                        val updatedList = if (reset) newVenues else it.displayedVenues + newVenues
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            displayedVenues = updatedList,
                            filteredVenues = updatedList,
                            hasMore = page < totalPages,
                            currentPage = page
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            error = result.message
                        )
                    }
                }
                Result.Loading -> {}
            }
        }
    }
}
