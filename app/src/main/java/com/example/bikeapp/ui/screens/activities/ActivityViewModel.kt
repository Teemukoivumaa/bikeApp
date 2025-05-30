package com.example.bikeapp.ui.screens.activities

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeapp.data.local.AppDatabase
import com.example.bikeapp.data.model.StravaActivityEntity
import com.example.bikeapp.utils.calculateEndTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

// Sample ViewModel for managing the UI state and database operations
class ActivityViewModel(private val database: AppDatabase) : ViewModel() {

    private val _activities = MutableStateFlow<List<StravaActivityEntity>>(emptyList())
    private val _totalLength = MutableStateFlow(0.0f)
    val activities: StateFlow<List<StravaActivityEntity>> = _activities
    val totalLength: StateFlow<Float> = _totalLength

    init {
        loadActivities()
        observeActivities() // Call observeActivities instead of calculateTotalLength here
    }

    // Load activities from the database when the ViewModel is initialized
    private fun loadActivities() {
        viewModelScope.launch {
            database.stravaActivityDao().getAllActivitiesSortedByDate().collect {
                _activities.value = it
            }
        }
    }

    // Observe the activities flow and recalculate total length when it changes
    private fun observeActivities() {
        viewModelScope.launch {
            activities.collect {
                calculateTotalLength(it)
            }
        }
    }

    // Recalculate total length based on the provided list of activities
    private fun calculateTotalLength(activities: List<StravaActivityEntity>) {
        val totalLength = activities.sumOf { it.distance.toDouble() }.toFloat()
        _totalLength.value = totalLength
    }

    // Add a new activity to the database
    fun addActivity(name: String, date: Date = Date()) {
        var distance = 7092.39990234375

        viewModelScope.launch {
            val newActivity = StravaActivityEntity(
                id = 0, // Assuming auto-increment in the database
                name = name,
                startDate = date,
                distance = distance.toFloat(),
                type = "Ride",
                movingTime = 1000,
                elapsedTime = 200,
                activityEndTime = "12:00",
                averageSpeed = 20.0F,
                maxSpeed = 25.0F,
                totalElevationGain = 500F,
                averageWatts = 5F,
                externalId = "externalId",
                description = "desc",
                calories = 200F,
                sportType = "sport",
                elevHigh = 100F,
                elevLow = 10F,
                deviceName = "Mock",
                averageHeartrate = 10.0f,
                maxHeartrate = 20.0f,
            )

            Log.d("ActivityViewModel", "Adding activity: $newActivity")

            database.stravaActivityDao().insert(newActivity)

            loadActivities() // Refresh the list after adding
        }
    }

    // Delete an activity from the database
    fun deleteActivity(activity: StravaActivityEntity) {
        viewModelScope.launch {
            database.stravaActivityDao().deleteActivityById(activity.id)
            loadActivities() // Refresh the list after deleting
        }
    }
}

