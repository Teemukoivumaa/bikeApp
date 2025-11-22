package com.example.bikeapp.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeapp.data.local.AppDatabase
import com.example.bikeapp.data.model.AthleteEntity
import com.example.bikeapp.data.model.ChallengeEntity
import com.example.bikeapp.data.model.StravaActivityEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val database: AppDatabase) : ViewModel() {

    private val _latestRides = MutableStateFlow<List<StravaActivityEntity>>(emptyList())
    val latestRides: StateFlow<List<StravaActivityEntity>> = _latestRides.asStateFlow()

    private val _ridesToday = MutableStateFlow<List<StravaActivityEntity>>(emptyList())
    val ridesToday: StateFlow<List<StravaActivityEntity>> = _ridesToday.asStateFlow()

    private val _weeklyStats = MutableStateFlow<Map<String, Number>>(emptyMap())
    val weeklyStats: StateFlow<Map<String, Number>> = _weeklyStats.asStateFlow()

    private val _activeChallenge = MutableStateFlow<ChallengeEntity?>(null)
    val activeChallenge: StateFlow<ChallengeEntity?> = _activeChallenge.asStateFlow()

    private val _athleteEntity = MutableStateFlow<AthleteEntity?>(null)
    val athleteEntity: StateFlow<AthleteEntity?> = _athleteEntity.asStateFlow()

    init {
        fetchAthlete()
        fetchLatestRides()
        fetchRidesForToday()
        calculateWeeklyStats()
        fetchActiveChallenge()
    }

    private fun fetchAthlete() {
        viewModelScope.launch {
            val athlete = database.athleteDao().getAthlete()

            if (athlete != null) {
                _athleteEntity.value = athlete
            }
        }
    }

    private fun fetchActiveChallenge() {
        viewModelScope.launch {
            database.challengeDao().getActiveChallenges()
                .catch { e ->
                    Log.e("HomeScreenViewModel", "Error fetching active challenge", e)
                }
                .collect { challenges ->
                    if (challenges.isNotEmpty()) {
                        _activeChallenge.value = challenges.first()
                    }
                }
        }
    }

    private fun fetchLatestRides() {
        viewModelScope.launch {
            database.stravaActivityDao()
                .getLatestActivities(limit = 3) // Assuming this returns Flow<List<StravaActivityEntity>>
                .catch { e ->
                    Log.e("HomeScreenViewModel", "Error collecting latest rides:", e)
                    _latestRides.value = emptyList()
                }
                .collect { rides ->
                    Log.d("HomeScreenViewModel", "Latest rides: $rides")
                    if (rides.isNotEmpty()) {
                        _latestRides.value = rides
                    } else {
                        _latestRides.value = emptyList()
                    }
                }
        }
    }

    private fun fetchRidesForToday() {
        viewModelScope.launch {
            val now = Calendar.getInstance()
            val hoursToSubtract = now.get(Calendar.HOUR_OF_DAY)
            val minutesToSubtract = now.get(Calendar.MINUTE)

            val startOfTodayMillis = System.currentTimeMillis() -
                    TimeUnit.HOURS.toMillis(hoursToSubtract.toLong()) -
                    TimeUnit.MINUTES.toMillis(minutesToSubtract.toLong())
            database.stravaActivityDao()
                .getActivitiesByDate(
                    startDate = startOfTodayMillis, // Calculated start time of today (00:00)
                    endDate = System.currentTimeMillis() // Current time
                )
                .catch { e ->
                    Log.e("HomeScreenViewModel", "Error collecting rides for today:", e)
                    _ridesToday.value = emptyList()
                }
                .collect { rides ->
                    Log.d("HomeScreenViewModel", "Rides for today: $rides")
                    if (rides.isNotEmpty()) {
                        _ridesToday.value = rides
                    } else {
                        _ridesToday.value = emptyList()
                    }
                }
        }
    }

    /**
     * Calculates the weekly stats for the user.
     *
     * The weekly stats include:
     * - Total distance
     * - Total time
     * - Average speed
     *
     * @return A map containing the weekly stats.
     */
    private fun calculateWeeklyStats() {
        viewModelScope.launch {
            val stats = mutableMapOf<String, Number>()
            val now = Calendar.getInstance()

            // Calculate the start and end dates for the week
            // TODO: Create a way for the user to select the start and end dates in settings
            val startOfWeek = (now.clone() as Calendar).apply {
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            }

            val endOfWeek = (now.clone() as Calendar).apply {
                set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                add(Calendar.WEEK_OF_YEAR, 1) // Move to next Sunday
            }

            Log.d("HomeScreenViewModel", "Start of week: ${startOfWeek.timeInMillis}")
            Log.d("HomeScreenViewModel", "End of week: ${endOfWeek.timeInMillis}")

            // Query the database for activities within the week
            database.stravaActivityDao()
                .getActivitiesByDate(
                    startDate = startOfWeek.timeInMillis,
                    endDate = endOfWeek.timeInMillis
                )
                .catch { e ->
                    Log.e("HomeScreenViewModel", "Error collecting activities for week:", e)
                }
                .collect { activities ->
                    Log.d("HomeScreenViewModel", "Activities for week: $activities")

                    if (!activities.isNotEmpty()) {
                        stats["totalDistance"] = 0f
                        stats["totalTime"] = 0
                        stats["averageSpeed"] = 0f

                        _weeklyStats.value = stats
                        return@collect
                    }

                    var totalDistance = 0f
                    var totalTime = 0
                    var totalSpeed = 0f

                    activities.forEach { activity ->
                        totalDistance += activity.distance
                        totalTime += activity.elapsedTime
                        totalSpeed += activity.averageSpeed
                    }

                    stats["totalDistance"] = totalDistance
                    stats["totalTime"] = totalTime
                    stats["averageSpeed"] = totalSpeed / activities.size

                    _weeklyStats.value = stats
                }
        }

    }

}