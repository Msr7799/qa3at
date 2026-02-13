package com.example.qa3at.ui.screens.venue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qa3at.data.repository.VenueRepository
import com.example.qa3at.domain.model.Venue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class VenueDetailUiState(
    val isLoading: Boolean = false,
    val venue: Venue? = null,
    val error: String? = null
)

@HiltViewModel
class VenueDetailViewModel @Inject constructor(
    private val venueRepository: com.example.qa3at.data.repository.VenueRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VenueDetailUiState(isLoading = true))
    val uiState: StateFlow<VenueDetailUiState> = _uiState.asStateFlow()

    fun loadVenue(venueId: String) {
        if (venueId.isBlank()) {
            _uiState.value = VenueDetailUiState(
                isLoading = false,
                venue = null,
                error = "معرف القاعة غير صالح"
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = venueRepository.getVenueDetails(venueId)
            
            when (result) {
                is com.example.qa3at.data.repository.Result.Success -> {
                    _uiState.value = VenueDetailUiState(
                        isLoading = false,
                        venue = result.data,
                        error = null
                    )
                }
                is com.example.qa3at.data.repository.Result.Error -> {
                    _uiState.value = VenueDetailUiState(
                        isLoading = false,
                        venue = null,
                        error = result.message ?: "القاعة غير موجودة"
                    )
                }
                else -> {
                    // Should not happen for this call
                    _uiState.value = VenueDetailUiState(
                        isLoading = false,
                        venue = null,
                        error = "خطأ غير معروف"
                    )
                }
            }
        }
    }
}
