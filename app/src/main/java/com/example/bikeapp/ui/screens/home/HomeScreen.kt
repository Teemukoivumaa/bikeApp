package com.example.bikeapp.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bikeapp.R
import com.example.bikeapp.utils.convertMsToKmh
import com.example.bikeapp.utils.convertMtoKm
import com.example.bikeapp.utils.formatDate
import com.example.bikeapp.utils.formatDuration


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val scrollState = rememberScrollState()

    val viewModel: HomeScreenViewModel = hiltViewModel()

    val latestRidesFlow by viewModel.latestRides.collectAsState()
    val latestRides = latestRidesFlow.map { it }

    val ridesTodayFlow by viewModel.ridesToday.collectAsState()
    val ridesToday = ridesTodayFlow.map { it }
    val kmToday = convertMtoKm(ridesToday.sumOf { ride -> ride.distance.toDouble()  }.toFloat())

    val weeklyStatsFlow by viewModel.weeklyStats.collectAsState()
    val weeklyStats = weeklyStatsFlow.map { it }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bike App") },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Today's Ride Overview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        "Good day for a ride, User!",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = "Location",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Tornio, Lapland, Finland",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Weather: Sunny", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Temperature: +15°C", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Wind: 2 m/s", style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Today's goal is to cycle 15 km!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "You have already cycled $kmToday today.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Latest Activities Snippet

                Text(
                    "Latest Rides",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    if (latestRides.isEmpty()) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth().align(Alignment.CenterHorizontally)) {
                            Text("Start cycling for the rides to show up here.")
                        }
                    } else {
                        Column(modifier = Modifier.padding(16.dp)) {
                            latestRides.forEachIndexed { index, ride ->
                                LastRideItem(
                                    date = "${formatDate(ride.startDate)} - ${ride.activityEndTime}", // Adjust formatting as needed
                                    name = ride.name,
                                    distance = convertMtoKm(ride.distance),
                                    duration = formatDuration(ride.elapsedTime)
                                )
                                if (index < latestRides.size - 1) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    HorizontalDivider()
                                    Spacer(modifier = Modifier.height(14.dp))
                                }
                            }
                        }
                    }
                }


            // Statistics Summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Weekly Stats",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (weeklyStats.isEmpty()) {
                            Text("No weekly stats found.")
                            return@Row
                        } else {
                            StatisticItem(
                                label = "Distance",
                                value = convertMtoKm(weeklyStats[0].value as Float)
                            )
                            StatisticItem(
                                label = "Time",
                                value = formatDuration(weeklyStats[1].value as Int)
                            )
                            StatisticItem(
                                label = "Avg Speed",
                                value = "${convertMsToKmh(weeklyStats[2].value as Float)} km/h"
                            )
                        }
                    }
                }
            }

            // Inspiring Nudges
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Weekly Challenge",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        "Cycle 50 km this week and feel awesome!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { /* TODO: Show challenges */ }) {
                        Text("View Challenges")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun LastRideItem(date: String, name: String, distance: String, duration: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(date, style = MaterialTheme.typography.bodySmall)
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.distance),
                    contentDescription = "Distance",
                    modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(distance, style = MaterialTheme.typography.bodyMedium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.time),
                    contentDescription = "Duration",
                    modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(duration, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun StatisticItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}