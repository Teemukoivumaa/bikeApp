package com.example.bikeapp.ui.screens.activities

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bikeapp.R
import com.example.bikeapp.ui.components.DetailItem
import com.example.bikeapp.utils.convertMsToKmh
import com.example.bikeapp.utils.convertMtoKm
import com.example.bikeapp.utils.formatDate
import com.example.bikeapp.utils.formatDuration
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

data class DetailItem(val label: String, val value: String?)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailsScreen(
    viewModel: ActivitiesViewModel,
    activityId: Long,
) {
    val scrollState = rememberScrollState()
    val activityState = viewModel.activity.collectAsState(initial = null)
    val activity = activityState.value

    val locationsState = viewModel.locations.collectAsState(initial = emptyList())
    val activityLocations = locationsState.value

    LaunchedEffect(activityId) {
        viewModel.loadActivity(activityId)
    }

    if (activity == null) {
        Text(text = "Activity not found")
        return
    }

    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Hero Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.bike),
                        contentDescription = activity.type,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = activity.name, style = MaterialTheme.typography.headlineSmall)
                }

                Text(
                    text = "Activity type: ${activity.type}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                if (activity.description != null && activity.description.isNotBlank()) {
                    Text(
                        text = activity.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Text(
                    text = "${formatDate(activity.startDate)} - ${activity.activityEndTime}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Key Metrics at a Glance
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    DetailItem(
                        icon = R.drawable.distance,
                        label = "Distance",
                        value = convertMtoKm(activity.distance)
                    )
                    DetailItem(
                        icon = R.drawable.time,
                        label = "Duration",
                        value = formatDuration(activity.elapsedTime)
                    )
                    DetailItem(
                        icon = R.drawable.altitude,
                        label = "Elevation gain",
                        value = convertMtoKm(activity.totalElevationGain)
                    )
                }

                // Detailed Breakdown
                Text(
                    text = "Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                // Detailed breakdown items
                val detailsList = listOfNotNull(
                    DetailItem("Max Speed", "${convertMsToKmh(activity.maxSpeed)} km/h"),
                    DetailItem("Avg speed", "${convertMsToKmh(activity.averageSpeed)} km/h"),
                    DetailItem("Elevation High", "${activity.elevHigh} m"),
                    DetailItem("Elevation Low", "${activity.elevLow} m"),
                    activity.maxHeartrate?.let { DetailItem("Max Heart Rate", "$it bpm") },
                    activity.averageHeartrate?.let {
                        DetailItem(
                            "Average Heart Rate",
                            "$it bpm"
                        )
                    },
                    activity.calories?.let { DetailItem("Calories", "$it kcal") },
                    activity.averageWatts?.let { DetailItem("Average Watts", "$it W") },
                    activity.deviceName?.let { DetailItem("Device", it) }
                )

                DynamicDetailTable(details = detailsList)

                if (activity.externalId != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "External ID",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = activity.externalId,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }


                if (activityLocations.isNotEmpty() == true) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Location",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    val startingLocation = activityLocations.first()
                    val endingLocation = activityLocations.last()

                    Text(
                        text = "Start Location: ${startingLocation.coordinatesAsString}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "End Location: ${endingLocation.coordinatesAsString}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    val startLocationCoordinates =
                        LatLng(startingLocation.latitude, startingLocation.longitude)
                    val endLocationCoordinates =
                        LatLng(endingLocation.latitude, endingLocation.longitude)

                    val startMarkerState =
                        remember { MarkerState(position = startLocationCoordinates) }
                    val endMarkerState = remember { MarkerState(position = endLocationCoordinates) }

                    val cameraPositionState = rememberCameraPositionState {
                        position =
                            CameraPosition.fromLatLngZoom(startLocationCoordinates, 13f)
                    }

                    GoogleMap(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        cameraPositionState = cameraPositionState
                    ) {
                        Marker(
                            state = startMarkerState,
                            title = activity.name,
                            snippet = "Start Location",
                        )

                        Marker(
                            state = endMarkerState,
                            title = activity.name,
                            snippet = "End Location",
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun DynamicDetailTable(details: List<DetailItem>) {
    if (details.isNotEmpty()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                details.chunked(2).forEach { rowItems ->
                    DetailRowGrid(
                        label = rowItems.first().label,
                        value = rowItems.first().value ?: ""
                    )
                    if (rowItems.size > 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            if (details.size > 1) {
                Column(modifier = Modifier.weight(1f)) {
                    details.drop(1).chunked(2).forEach { rowItems ->
                        if (rowItems.isNotEmpty()) {
                            DetailRowGrid(
                                label = rowItems.first().label,
                                value = rowItems.first().value ?: ""
                            )
                        }
                        if (rowItems.size > 1) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRowGrid(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}