package com.example.bikeapp.ui.screens.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeapp.data.local.AppDatabase
import com.example.bikeapp.data.model.StravaActivityEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

// Sample ViewModel for managing the UI state and database operations
class ActivityViewModel(private val database: AppDatabase) : ViewModel() {

    private val _activities = MutableStateFlow<List<StravaActivityEntity>>(emptyList())
    val activities: StateFlow<List<StravaActivityEntity>> = _activities

    init {
        loadActivities()
    }

    // Load activities from the database when the ViewModel is initialized
    private fun loadActivities() {
        viewModelScope.launch {
            database.stravaActivityDao().getAllActivitiesSortedByDate().collect {
                _activities.value = it
            }
        }
    }

    // Add a new activity to the database
    fun addActivity(name: String, date: Date = Date(), distance: Float = 0.0f) {
        viewModelScope.launch {
            val newActivity = StravaActivityEntity(
                id = 0, // Assuming auto-increment in the database
                name = name,
                startDate = date,
                distance = distance,
                type = "Ride",
                movingTime = 10,
                elapsedTime = 20,
                averageSpeed = 2.0F,
                maxSpeed = 5.0F,
                totalElevationGain = 500F,
                averageWatts = 5F,
                externalId = "externalId"
            )
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

