package com.example.bikeapp.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bikeapp.utils.convertMsToKmh
import com.example.bikeapp.utils.convertMtoKm
import com.example.bikeapp.utils.formatDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeScreenViewModel) {
    val recentRideState = viewModel.recentRide.collectAsState(initial = null)
    val recentRide = recentRideState.value // Access the value property

    var newActivityName by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Recent Ride Statistics", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (recentRide?.name !== "mock" && recentRide != null) {
                StatisticItem(label = "Distance", value = convertMtoKm(recentRide.distance))
                StatisticItem(
                    label = "Duration", value = formatDuration(recentRide.elapsedTime)
                )
                StatisticItem(
                    label = "Average Speed",
                    value = "${convertMsToKmh(recentRide.averageSpeed)} km/h"
                )
            } else {
                Text("No recent ride data available.")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { showBottomSheet = true }) {
                Text("New Activity")
            }
        }
    }

    if (showBottomSheet) {
        AlertDialog(
            onDismissRequest = { showBottomSheet = false },
            title = { Text("Add New Activity") },
            text = {

                OutlinedTextField(
                    value = newActivityName,
                    onValueChange = { newActivityName = it },
                    label = { Text("Activity Name") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.addActivity(name = newActivityName)
                    newActivityName = ""
                    showBottomSheet = false
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = { showBottomSheet = false }) {
                    Text("Cancel")
                }
            }
        )
//        ModalBottomSheet(
//            onDismissRequest = { showBottomSheet = false },
//            sheetState = rememberModalBottomSheetState()
//        ) {
//            NewActivitySheet()
//        }
    }
}

@Composable
fun StatisticItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun NewActivitySheet() {
    var activityName: String by remember { mutableStateOf("") }
    var activityDistance: String by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("New Activity")
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = activityName,
            onValueChange = { activityName = it },
            label = { Text("Activity Name") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = activityDistance,
            onValueChange = { activityDistance = it },
            label = { Text("Activity Distance") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { /* Handle activity creation */ }) {
            Text("Create Activity")
        }
    }
}