package com.example.bikeapp.ui.screens.activities

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeapp.data.local.AppDatabase
import com.example.bikeapp.data.model.StravaActivityEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActivityViewModel(private val database: AppDatabase) : ViewModel() {

    private val _activities = MutableStateFlow<List<StravaActivityEntity>>(emptyList())
    private val _totalLength = MutableStateFlow(0.0f)
    val activities: StateFlow<List<StravaActivityEntity>> = _activities
    val totalLength: StateFlow<Float> = _totalLength

    // Initialize the ViewModel and load activities when the ViewModel is created
    init {
        refreshActivities()
    }

    fun refreshActivities() {
        Log.d("ActivityViewModel", "Refreshing activities")
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
}

