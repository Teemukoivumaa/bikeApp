package com.example.bikeapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeapp.data.local.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Represents the state of the Profile screen
enum class ChartView {
    DISTANCE,
    WORKOUTS,
    ELEVATION
}

data class ProfileScreenUiState(
    val isLoading: Boolean = false,
    val userName: String? = null,
    val profileImageUrl: String? = null,
    val totalDistance: Double? = null,
    val totalWorkouts: Int? = null,
    val totalMovingTime: Int? = null,
    val totalElevationGain: Double? = null,
    val monthlyDistance: List<Double> = emptyList(),
    val monthlyWorkouts: List<Double> = emptyList(),
    val monthlyElevation: List<Double> = emptyList(),
    val monthlyProgressLabels: List<String> = emptyList(),
    val selectedChart: ChartView = ChartView.DISTANCE,
    // Add other profile-related data as needed
    val errorMessage: String? = null
)

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val database: AppDatabase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileScreenUiState())
    val uiState: StateFlow<ProfileScreenUiState> = _uiState.asStateFlow()

    // Load profile data when the ViewModel is created
    init {
        loadProfileData()
    }

    fun onChartSelectionChanged(chartView: ChartView) {
        _uiState.update { it.copy(selectedChart = chartView) }
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

                val activities = database.stravaActivityDao().getAllActivities().first()
                val totalDistance =
                    activities.sumOf { it.distance.toDouble() } / 1000 // distance is in meters, result in km
                val totalWorkouts = activities.size
                val totalMovingTime = activities.sumOf { it.movingTime }
                val totalElevationGain = activities.sumOf { it.totalElevationGain.toDouble() }

                val monthlyStats = activities
                    .groupBy {
                        val cal = Calendar.getInstance()
                        cal.time = it.startDate
                        cal.get(Calendar.YEAR) to cal.get(Calendar.MONTH)
                    }
                    .mapValues { entry ->
                        val distance = entry.value.sumOf { it.distance / 1000.0 }
                        val workoutCount = entry.value.size
                        val elevation = entry.value.sumOf { it.totalElevationGain.toDouble() }
                        Triple(distance, workoutCount, elevation)
                    }
                    .toSortedMap(compareByDescending<Pair<Int, Int>> { it.first }.thenByDescending { it.second })
                    .toList()
                    .take(6)
                    .reversed()

                val monthlyProgressLabels = monthlyStats.map {
                    val (year, month) = it.first
                    val cal = Calendar.getInstance().apply { set(year, month, 1) }
                    SimpleDateFormat("MMM", Locale.getDefault()).format(cal.time)
                }
                val monthlyDistance = monthlyStats.map { it.second.first }
                val monthlyWorkouts = monthlyStats.map { it.second.second.toDouble() }
                val monthlyElevation = monthlyStats.map { it.second.third }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userName = "${athlete.firstname} ${athlete.lastname}",
                        totalDistance = totalDistance,
                        totalWorkouts = totalWorkouts,
                        totalMovingTime = totalMovingTime,
                        totalElevationGain = totalElevationGain,
                        monthlyDistance = monthlyDistance,
                        monthlyWorkouts = monthlyWorkouts,
                        monthlyElevation = monthlyElevation,
                        monthlyProgressLabels = monthlyProgressLabels
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