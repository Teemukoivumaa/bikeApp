package com.example.bikeapp.ui.screens.activities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bikeapp.data.model.StravaActivityEntity
import com.example.bikeapp.utils.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(viewModel: ActivityViewModel) {
    var newActivityName by remember { mutableStateOf("") }
    var newActivityDistance by remember { mutableStateOf("") }
    val activities by viewModel.activities.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Activities") }) },
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
//                OutlinedTextField(
//                    value = newActivityName,
//                    onValueChange = { newActivityName = it },
//                    label = { Text("Activity Name") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//                Spacer(modifier = Modifier.size(8.dp))
//                OutlinedTextField(
//                    value = newActivityDistance,
//                    onValueChange = { newActivityDistance = it },
//                    label = { Text("Distance") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//                Spacer(modifier = Modifier.size(16.dp))
//                Button(onClick = {
//                    viewModel.addActivity(
//                        name = newActivityName,
//                        distance = newActivityDistance.toFloatOrNull() ?: 0.0f
//                    )
//                    newActivityName = ""
//                    newActivityDistance = ""
//                }) {
//                    Text("Add Activity")
//                }
                Spacer(modifier = Modifier.size(16.dp))
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(activities) { activity ->
                        ActivityCard(
                            activity = activity,
                            onDelete = { viewModel.deleteActivity(activity) })
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityCard(
    activity: StravaActivityEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = activity.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Date: ${formatDate(activity.startDate)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Distance: ${activity.distance}m",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(onClick = onDelete) {
                Text("Delete")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActivityCardPreview() {
    val activity = StravaActivityEntity(
        id = 1,
        name = "Sample Activity",
        type = "Ride",
        distance = 10.0f,
        movingTime = 60,
        elapsedTime = 60,
        startDate = java.util.Date(),
        averageSpeed = 10.0f,
        maxSpeed = 10.0f,
        totalElevationGain = 10.0f,
        averageWatts = 10.0f,
        externalId = "externalId"
    )
    ActivityCard(activity = activity, onDelete = { /* Handle delete action here */ })
}
