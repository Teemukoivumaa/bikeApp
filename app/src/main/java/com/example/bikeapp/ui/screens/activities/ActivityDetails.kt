package com.example.bikeapp.ui.screens.activities

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bikeapp.R
import com.example.bikeapp.data.model.ActivityLocationEntity
import com.example.bikeapp.data.model.StravaActivityEntity
import com.example.bikeapp.utils.convertMsToKmh
import com.example.bikeapp.utils.convertMtoKm
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailsPage(
    activity: Flow<StravaActivityEntity>,
    locations: Flow<List<ActivityLocationEntity>>
) {
    val usableActivity = activity.collectAsState(initial = null).value
    val usableLocations = locations.collectAsState(initial = null).value

    val scrollState = rememberScrollState()

    if (usableActivity == null) {
        CircularProgressIndicator()
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
                    val sportIcon = getSportIcon(usableActivity.type)
                    if (sportIcon != null) {
                        Icon(
                            imageVector = sportIcon,
                            contentDescription = usableActivity.type,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(text = usableActivity.name, style = MaterialTheme.typography.headlineSmall)
                }
                Text(
                    text = SimpleDateFormat(
                        "dd.MM.yyyy HH:mm",
                        Locale.getDefault()
                    ).format(usableActivity.startDate),
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
                        value = convertMtoKm(usableActivity.distance)
                    )
                    DetailItem(
                        icon = R.drawable.time,
                        label = "Duration",
                        value = formatTime(usableActivity.elapsedTime)
                    )
                    DetailItem(
                        icon = R.drawable.avg_speed,
                        label = "Avg Speed",
                        value = "${convertMsToKmh(usableActivity.averageSpeed)} km/h"
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

                DetailRow(label = "Sport Type", value = usableActivity.sportType)
                DetailRow(label = "Moving Time", value = formatTime(usableActivity.movingTime))
                DetailRow(
                    label = "Max Speed",
                    value = "${convertMsToKmh(usableActivity.maxSpeed)} km/h"
                )
                DetailRow(
                    label = "Total Elevation Gain",
                    value = "${String.format("%.1f", usableActivity.totalElevationGain)} m"
                )
                DetailRow(
                    label = "Elevation High",
                    value = "${String.format("%.1f", usableActivity.elevHigh)} m"
                )
                DetailRow(
                    label = "Elevation Low",
                    value = "${String.format("%.1f", usableActivity.elevLow)} m"
                )

                if (usableActivity.averageWatts != null) {
                    DetailRow(
                        label = "Average Watts",
                        value = "${String.format("%.0f", usableActivity.averageWatts)} W"
                    )
                }
                if (usableActivity.averageHeartrate != null) {
                    DetailRow(
                        label = "Average Heart Rate",
                        value = "${String.format("%.0f", usableActivity.averageHeartrate)} bpm"
                    )
                }
                if (usableActivity.maxHeartrate != null) {
                    DetailRow(
                        label = "Max Heart Rate",
                        value = "${String.format("%.0f", usableActivity.maxHeartrate)} bpm"
                    )
                }
                if (usableActivity.calories != null) {
                    DetailRow(
                        label = "Calories",
                        value = "${String.format("%.0f", usableActivity.calories)} kcal"
                    )
                }
                if (usableActivity.deviceName != null) {
                    DetailRow(label = "Device", value = usableActivity.deviceName)
                }
                if (usableActivity.description != null && usableActivity.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = usableActivity.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                if (usableActivity.externalId != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "External ID",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = usableActivity.externalId,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }

                Log.d("ActivityDetailsPage", "Locations: $usableLocations")
                if (usableLocations?.isNotEmpty() == true) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Location",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    val startingLocation = usableLocations.first()
                    val endingLocation = usableLocations.last()

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


                    val startLocationCoordinates = LatLng(startingLocation.latitude, startingLocation.longitude)
                    val endLocationCoordinates = LatLng(endingLocation.latitude, endingLocation.longitude)

                    val startMarkerState = remember { MarkerState(position = startLocationCoordinates) }
                    val endMarkerState = remember { MarkerState(position = endLocationCoordinates) }

                    val cameraPositionState = rememberCameraPositionState {
                        position =
                            CameraPosition.fromLatLngZoom(startLocationCoordinates, 15f) // Zoom-tasoa voi säätää
                    }

                    GoogleMap(
                         modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        cameraPositionState = cameraPositionState
                    ) {
                        Marker(
                            state = startMarkerState,
                            title = usableActivity.name,
                            snippet = "Start Location",
                        )
                        Marker(
                            state = endMarkerState,
                            title = usableActivity.name,
                            snippet = "End Location",
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun DetailItem(icon: Int, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

fun formatTime(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}

fun getSportIcon(sportType: String): ImageVector? {
    return when (sportType.lowercase(Locale.getDefault())) {
        "bike", "ride" -> Icons.Filled.LocationOn
        "run" -> Icons.Filled.LocationOn // Placeholder
        "walk" -> Icons.Filled.LocationOn // Placeholder
        else -> null
    }
}