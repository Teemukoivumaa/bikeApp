package com.example.bikeapp.ui.screens.activities

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bikeapp.R
import com.example.bikeapp.data.model.ActivityLocationEntity
import com.example.bikeapp.data.model.ActivityStreamEntity
import com.example.bikeapp.data.model.StravaActivityEntity
import com.example.bikeapp.data.remote.StreamType
import com.example.bikeapp.ui.components.DetailItem
import com.example.bikeapp.utils.convertMsToKmh
import com.example.bikeapp.utils.convertMtoKm
import com.example.bikeapp.utils.formatDate
import com.example.bikeapp.utils.formatDuration
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailsScreen(
    activityId: Long,
    navController: NavController
) {
    val viewModel: ActivitiesViewModel = hiltViewModel()
    val scrollState = rememberScrollState()
    val activity by viewModel.activity.collectAsState(initial = null)
    val activityLocations by viewModel.locations.collectAsState(initial = emptyList())
    val activityStreams by viewModel.streams.collectAsState(initial = emptyList())

    LaunchedEffect(activityId) {
        viewModel.loadActivity(activityId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        activity?.let {
                            Icon(
                                painter = painterResource(id = R.drawable.bike),
                                contentDescription = it.type,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = it.name, style = MaterialTheme.typography.headlineSmall)
                        } ?: Text("Loading...", style = MaterialTheme.typography.headlineSmall)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (activity == null) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(500)),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Activity type: ${activity!!.type}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )

                            if (activity!!.description != null && activity!!.description!!.isNotBlank()) {
                                Text(
                                    text = activity!!.description!!,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            Text(
                                text = "${formatDate(activity!!.startDate)} - ${activity!!.activityEndTime}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Key Metrics at a Glance
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    DetailItem(
                                        icon = R.drawable.distance,
                                        label = "Distance",
                                        value = convertMtoKm(activity!!.distance)
                                    )
                                    DetailItem(
                                        icon = R.drawable.time,
                                        label = "Duration",
                                        value = formatDuration(activity!!.elapsedTime)
                                    )
                                    DetailItem(
                                        icon = R.drawable.altitude,
                                        label = "Elevation gain",
                                        value = convertMtoKm(activity!!.totalElevationGain)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Detailed Breakdown
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Details",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    HorizontalDivider()
                                    Spacer(modifier = Modifier.height(8.dp))

                                    val detailsList = listOfNotNull(
                                        "Max Speed" to "${convertMsToKmh(activity!!.maxSpeed)} km/h",
                                        "Avg Speed" to "${convertMsToKmh(activity!!.averageSpeed)} km/h",
                                        "Max Elev" to "${activity!!.elevHigh} m",
                                        "Min Elev" to "${activity!!.elevLow} m",
                                        activity!!.maxHeartrate?.let { "Max HR" to "$it bpm" },
                                        activity!!.averageHeartrate?.let { "Avg HR" to "$it bpm" },
                                        activity!!.calories?.let { "Calories" to "$it kcal" },
                                        activity!!.averageWatts?.let { "Avg Watts" to "$it W" },
                                        activity!!.deviceName?.let { "Device" to it }
                                    )
                                    SimpleGrid(detailsList)
                                }
                            }


                            Spacer(modifier = Modifier.height(16.dp))

                            ActivityCharts(
                                viewModel = viewModel,
                                activityStreams = activityStreams
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            ActivityMap(
                                activity = activity!!,
                                activityLocations = activityLocations
                            )

                            if (activity!!.externalId != null) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "External ID",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = activity!!.externalId!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityCharts(
    viewModel: ActivitiesViewModel,
    activityStreams: List<ActivityStreamEntity>
) {
    if (activityStreams.isEmpty()) return

    val availableStreamTypes = remember(activityStreams) {
        val chartableStreams = listOf(
            StreamType.HEARTRATE,
            StreamType.ALTITUDE,
            StreamType.VELOCITY_SMOOTH,
            StreamType.CADENCE,
            StreamType.WATTS,
            StreamType.TEMP,
            StreamType.GRADE_SMOOTH
        )
        activityStreams.map { it.type }.filter { it in chartableStreams }.sortedBy { it.name }
    }

    if (availableStreamTypes.isEmpty()) return

    var selectedStreamType by remember { mutableStateOf(availableStreamTypes.first()) }

    LaunchedEffect(selectedStreamType, activityStreams) {
        viewModel.processChartData(selectedStreamType)
    }

    val chartData by viewModel.chartData.collectAsState()
    val xLabels by viewModel.xLabels.collectAsState()
    val yAxisLabel by viewModel.yAxisLabel.collectAsState()

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Charts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableStreamTypes.forEach {
                    FilterChip(
                        selected = it == selectedStreamType,
                        onClick = { selectedStreamType = it },
                        label = {
                            Text(
                                it.name.replace("_", " ").lowercase()
                                    .replaceFirstChar { char -> char.titlecase() })
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val chartColor = colorResource(id = R.color.vibrant_green)

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LineChart(
                    modifier = Modifier
                        .height(300.dp),
                    data = remember(yAxisLabel, chartData) {
                        listOf(
                            Line(
                                label = yAxisLabel,
                                values = chartData,
                                color = SolidColor(chartColor),
                                firstGradientFillColor = chartColor,
                                secondGradientFillColor = chartColor.copy(alpha = 0.5f),
                                dotProperties = DotProperties(
                                    enabled = false,
                                )
                            )
                        )
                    },
                    animationMode = AnimationMode.Together(delayBuilder = {
                        it * 2L
                    }),
                    indicatorProperties = HorizontalIndicatorProperties(
                        enabled = true,
                        textStyle = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White
                        ),
                        contentBuilder = {
                            ceil(it).toString()
                        }
                    ),
                    labelProperties = LabelProperties(
                        enabled = false
                    ),
                    labelHelperProperties = LabelHelperProperties(
                        enabled = true,
                        textStyle = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White
                        ),
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    xLabels.forEach { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Time (hh:mm:ss)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun ActivityMap(
    activity: StravaActivityEntity,
    activityLocations: List<ActivityLocationEntity>
) {
    var decodedPolyline: List<LatLng?> = emptyList()
    if (activity.summaryPolyline != null) {
        decodedPolyline = PolyUtil.decode(activity.summaryPolyline)
    }

    if (activityLocations.isNotEmpty() || decodedPolyline.isNotEmpty()) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Route",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                val startingLocation = activityLocations.firstOrNull()
                val endingLocation = activityLocations.lastOrNull()

                val startLocationCoordinates =
                    startingLocation?.let { LatLng(it.latitude, it.longitude) } ?: decodedPolyline.first()

                val endLocationCoordinates =
                    endingLocation?.let { LatLng(it.latitude, it.longitude) } ?: decodedPolyline.last()

                val stopAndStartAreSame = startLocationCoordinates == endLocationCoordinates

                val startMarkerState =
                    startLocationCoordinates?.let { MarkerState(position = it) }
                val endMarkerState = endLocationCoordinates?.let { MarkerState(position = it) }

                val cameraPositionState = rememberCameraPositionState {
                    startLocationCoordinates?.let {
                        position =
                            CameraPosition.fromLatLngZoom(it, 13f)
                    }
                }

                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    cameraPositionState = cameraPositionState,
                    mapColorScheme = ComposeMapColorScheme.DARK,
                    uiSettings = MapUiSettings(scrollGesturesEnabled = false, zoomGesturesEnabled = false)
                ) {
                    if (stopAndStartAreSame) {
                        if (startMarkerState != null) {
                            AdvancedMarker(
                                state = startMarkerState,
                                title = activity.name,
                                snippet = "Start & End Location",
                            )
                        }
                    } else {
                        if (startMarkerState != null) {
                            AdvancedMarker(
                                state = startMarkerState,
                                title = activity.name,
                                snippet = "Start Location",
                            )
                        }

                        if (endMarkerState != null) {
                            AdvancedMarker(
                                state = endMarkerState,
                                title = activity.name,
                                snippet = "End Location",
                            )
                        }
                    }

                    if (decodedPolyline.isNotEmpty()) {
                        Polyline(
                            points = decodedPolyline as List<LatLng>,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            width = 5f
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleGrid(items: List<Pair<String, String>>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        items.chunked(2).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowItems.forEach { item ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp)
                    ) {
                        Text(text = item.first, style = MaterialTheme.typography.labelLarge)
                        Text(text = item.second, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                // Fill up empty space in the row if the number of items is odd
                if (rowItems.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
