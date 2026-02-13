package com.example.qa3at.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qa3at.data.repository.Result
import com.example.qa3at.data.repository.VenueRepository
import com.example.qa3at.domain.model.City
import com.example.qa3at.domain.model.SearchFilters
import com.example.qa3at.domain.model.TimeSlot
import com.example.qa3at.domain.model.Venue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val cities: List<City> = emptyList(),
    val timeSlots: List<TimeSlot> = emptyList(),
    val venues: List<Venue> = emptyList(),
    val filters: SearchFilters = SearchFilters(),
    val error: String? = null,
    val totalPages: Int = 1,
    val currentPage: Int = 1
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val venueRepository: VenueRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState(isLoading = true))
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Load filters data
            val citiesResult = venueRepository.getCities()
            val slotsResult = venueRepository.getTimeSlots()

            val cities = if (citiesResult is Result.Success) citiesResult.data else emptyList()
            val timeSlots = if (slotsResult is Result.Success) slotsResult.data else emptyList()

            _uiState.update { it.copy(cities = cities, timeSlots = timeSlots) }

            // Initial search
            searchVenues(reset = true)
        }
    }

    fun onFiltersChanged(newFilters: SearchFilters) {
        _uiState.update { it.copy(filters = newFilters) }
        searchVenues(reset = true)
    }

    fun loadMore() {
        if (_uiState.value.isLoading || _uiState.value.isLoadingMore) return
        if (_uiState.value.currentPage >= _uiState.value.totalPages) return
        
        searchVenues(reset = false)
    }

    private fun searchVenues(reset: Boolean) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (reset) {
                _uiState.update { it.copy(isLoading = true, venues = emptyList(), error = null, currentPage = 1) }
            } else {
                _uiState.update { it.copy(isLoadingMore = true, error = null) }
            }

            val currentState = _uiState.value
            val pageToLoad = if (reset) 1 else currentState.currentPage + 1
            val filters = currentState.filters

            // Map filters to parameters
            // Handle "all" city or empty string as null
            val cityParam = if (filters.city.isNotEmpty() && filters.city.lowercase() != "all") filters.city else null
            val typeParam = if (filters.type.isNotEmpty() && filters.type.lowercase() != "all") filters.type else null
            val dateParam = filters.date.takeIf { it.isNotEmpty() }
            val guestsParam = if (filters.guests > 0) filters.guests else null
            val slotIdParam = filters.slotId.takeIf { it.isNotEmpty() }
            val sortByParam = filters.sortBy.name.lowercase()

            val result = venueRepository.searchVenues(
                city = cityParam,
                type = typeParam,
                date = dateParam,
                guests = guestsParam,
                slotId = slotIdParam,
                sortBy = sortByParam,
                page = pageToLoad,
                limit = 6 // Request 6 items as per requirements
            )

            when (result) {
                is Result.Success -> {
                    val (newVenues, totalPages) = result.data
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            venues = if (reset) newVenues else it.venues + newVenues,
                            totalPages = totalPages,
                            currentPage = pageToLoad
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
