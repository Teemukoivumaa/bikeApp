package com.example.bikeapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeapp.data.local.AppDatabase
import com.example.bikeapp.data.model.StravaActivityEntity
import kotlinx.coroutines.flow.Flow
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

    private fun loadActivities() {
        viewModelScope.launch {
            database.stravaActivityDao().getAllActivities().collect {
                _activities.value = it
            }
        }
    }

    fun addActivity(name: String, date: Date = Date(), distance: Float = 0.0f) {
        viewModelScope.launch {
            val newActivity = StravaActivityEntity(
                id = 1, // Assuming auto-increment in the database
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
            database.stravaActivityDao().insertAll(listOf(newActivity))

            loadActivities() // Refresh the list after adding
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(viewModel: ActivityViewModel, paddingValues: PaddingValues) {
    var newActivityName by remember { mutableStateOf("") }
    var newActivityDistance by remember { mutableStateOf("") }
    val activities by viewModel.activities.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Strava Activities") }) },
        modifier = Modifier.padding(paddingValues)
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = newActivityName,
                    onValueChange = { newActivityName = it },
                    label = { Text("Activity Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.size(8.dp))
                OutlinedTextField(
                    value = newActivityDistance,
                    onValueChange = { newActivityDistance = it },
                    label = { Text("Distance") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.size(16.dp))
                Button(onClick = {
                    viewModel.addActivity(
                        name = newActivityName,
                        distance = newActivityDistance.toFloatOrNull() ?: 0.0f
                    )
                    newActivityName = ""
                    newActivityDistance = ""
                }) {
                    Text("Add Activity")
                }
                Spacer(modifier = Modifier.size(16.dp))
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(activities) { activity ->
                        ActivityItem(activity = activity)
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityItem(activity: StravaActivityEntity) {
    Row(modifier = Modifier.padding(8.dp)) {
        Column {
            Text(text = "Name: ${activity.name}", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "Date: ${activity.startDate}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Distance: ${activity.distance}km",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}