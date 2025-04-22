package com.example.bikeapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeapp.data.local.AppDatabase
import com.example.bikeapp.data.model.StravaActivityEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class HomeScreenViewModel(private val database: AppDatabase) : ViewModel() {

    private val _recentRide = MutableStateFlow<StravaActivityEntity>(
        StravaActivityEntity(
            id = 0,
            name = "mock",
            startDate = Date(),
            distance = 0.0f,
            type = "Ride",
            movingTime = 10,
            elapsedTime = 20,
            activityEndTime = "12:00",
            averageSpeed = 2.0F,
            maxSpeed = 5.0F,
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
    )
    val recentRide: StateFlow<StravaActivityEntity> = _recentRide

    init {
        loadRecentRide()
    }

    private fun loadRecentRide() {
        viewModelScope.launch {
            database.stravaActivityDao().getLatestActivity().collect {
                _recentRide.value = it
            }
        }
    }

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
                elapsedTime = 2000,
                activityEndTime = "12:00",
                averageSpeed = 2.0F,
                maxSpeed = 5.0F,
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
            database.stravaActivityDao().insert(newActivity)

            loadRecentRide() // Refresh the list after adding
        }
    }

}