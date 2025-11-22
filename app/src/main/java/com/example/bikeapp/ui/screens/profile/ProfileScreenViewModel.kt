package com.example.bikeapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeapp.data.local.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Represents the state of the Profile screen
data class ProfileScreenUiState(
    val isLoading: Boolean = false,
    val userName: String? = null,
    val profileImageUrl: String? = null,
    val totalDistance: Double? = null,
    // Add other profile-related data as needed
    val errorMessage: String? = null
)

@HiltViewModel
class ProfileScreenViewModel @Inject constructor (
    private val database: AppDatabase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileScreenUiState())
    val uiState: StateFlow<ProfileScreenUiState> = _uiState.asStateFlow()

    // Load profile data when the ViewModel is created
    init {
        loadProfileData()
    }

    fun loadProfileData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val athlete = database.athleteDao().getAthlete()

                if (athlete == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No athlete found"
                        )
                    }
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userName = "${athlete.firstname} ${athlete.lastname}",
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load profile data: ${e.message}"
                    )
                }
            }
        }
    }

}