package com.example.bikeapp.ui.screens.activities

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.bikeapp.R
import com.example.bikeapp.data.model.StravaActivityEntity
import com.example.bikeapp.data.model.mockActivity
import com.example.bikeapp.ui.components.StatisticCard
import com.example.bikeapp.ui.components.StatisticItemWithIcon
import com.example.bikeapp.ui.screens.strava.SharedAuthViewModel
import com.example.bikeapp.utils.convertMsToKmh
import com.example.bikeapp.utils.convertMtoKm
import com.example.bikeapp.utils.formatDate
import com.example.bikeapp.utils.formatDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(viewModel: ActivityViewModel, navController: NavHostController) {
    val context = LocalContext.current
    val sharedAuthViewModel: SharedAuthViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()

    val activities by viewModel.activities.collectAsState()
    val totalLength by viewModel.totalLength.collectAsState()

    val listState = rememberLazyListState()
    var showStatsCards by rememberSaveable { mutableStateOf(true) }

    val firstVisibleItemScrollOffset by remember {
        derivedStateOf { listState.firstVisibleItemScrollOffset }
    }

    LaunchedEffect(firstVisibleItemScrollOffset) {
        showStatsCards = firstVisibleItemScrollOffset == 0
        // Only when first started, check if token is valid and refresh if not
        sharedAuthViewModel.refreshStravaToken(coroutineScope = coroutineScope)
        // Listen for toast events and show them
        sharedAuthViewModel.toastEvents.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    val animatedHeight by animateFloatAsState(
        targetValue = if (showStatsCards) 150f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "Statistic Card Height Animation"
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Activities") }, actions = {
                IconButton(onClick = { sharedAuthViewModel.fetchActivities() }) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = "Refresh activities",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            })
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(animatedHeight.dp),
                ) {
                    if (showStatsCards || animatedHeight > 0f) {  // Only compose if visible or animating
                        StatisticCard(
                            title = "Total Rides",
                            value = activities.size.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        StatisticCard(
                            title = "Total Distance",
                            value = convertMtoKm(totalLength),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
                LazyColumn(
                    state = listState,
                ) {
                    items(activities) { activity ->
                        ActivityCard(
                            activity = activity,
                            onOpenDetailsPage = { selectedActivity ->
                                navController.navigate("activityDetails/${selectedActivity.id}")
                            }
                        )
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivityCard(
    activity: StravaActivityEntity,
    onOpenDetailsPage: (StravaActivityEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    var onShowBottomSheetChanged: (Boolean) -> Unit = remember {
        { newValue -> showBottomSheet = newValue }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onShowBottomSheetChanged(true) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = activity.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${formatDate(activity.startDate)} - ${activity.activityEndTime}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                StatisticItemWithIcon(
                    icon = R.drawable.distance,
                    text = convertMtoKm(activity.distance)
                )
                Spacer(modifier = Modifier.width(16.dp))
                StatisticItemWithIcon(
                    icon = R.drawable.time,
                    text = formatDuration(activity.elapsedTime)
                )
            }


        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { onShowBottomSheetChanged(false) },
            sheetState = sheetState
        ) {
            ActivityDetailsBottomSheetContent(
                activity = activity,
                onOpenDetailsPage = onOpenDetailsPage,
                onShowBottomSheetChanged = onShowBottomSheetChanged
            )
        }
    }
}

data class ActivityDetailItem(
    val label: String,
    val value: String,
    val icon: Int? = null, // Optional icon resource ID
    val tooltip: String? = null
)

@Composable
fun ActivityDetailsBottomSheetContent(
    activity: StravaActivityEntity,
    onOpenDetailsPage: (StravaActivityEntity) -> Unit,
    onShowBottomSheetChanged: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Top row details
        Row {
            Column(modifier = Modifier.weight(5f)) {
                Text(
                    text = activity.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatisticItemWithIcon(
                    icon = R.drawable.calendar,
                    text = "${formatDate(activity.startDate)} - ${activity.activityEndTime}"
                )
            }

            FloatingActionButton(
                onClick = {
                    onOpenDetailsPage(activity)
                    onShowBottomSheetChanged(false)
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.read_more),
                    contentDescription = "View details",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(24.dp)
                        .weight(1f)
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Quick glance details using the dynamic component
        val detailsList = listOf(
            ActivityDetailItem(
                label = "Distance",
                value = convertMtoKm(activity.distance),
                icon = R.drawable.distance,
                tooltip = "Distance cycled"
            ),
            ActivityDetailItem(
                label = "Time",
                value = formatDuration(activity.elapsedTime),
                icon = R.drawable.time,
                tooltip = "Time cycled"
            ),
            ActivityDetailItem(
                label = "Elevation Gain",
                value = "${activity.totalElevationGain} m",
                icon = R.drawable.altitude,
                tooltip = "Total elevation gain"
            ),
            ActivityDetailItem(
                label = "Avg Speed",
                value = "${convertMsToKmh(activity.averageSpeed)} km/h",
                icon = R.drawable.avg_speed,
                tooltip = "Average speed"
            ),
            ActivityDetailItem(
                label = "Max Speed",
                value = "${convertMsToKmh(activity.maxSpeed)} km/h",
                icon = R.drawable.max_speed,
                tooltip = "Max speed"
            ),
            ActivityDetailItem(
                label = "Max Heart Rate",
                value = "${activity.maxHeartrate} bpm",
                icon = R.drawable.max_heartrate,
                tooltip = "Max heartrate"
            ),
            ActivityDetailItem(
                label = "Avg Heart Rate",
                value = "${activity.averageHeartrate} bpm",
                icon = R.drawable.avg_heartrate,
                tooltip = "Average heartrate"
            )
        )

        DynamicActivityDetails(details = detailsList)
    }
}

@Composable
fun DynamicActivityDetails(details: List<ActivityDetailItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        details.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowItems.forEach { item ->
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (item.icon != null) {
                            Image(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.tooltip,
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Column {
                            Text(text = item.value)
                            item.tooltip?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ActivityCardPreview() {
    ActivityCard(activity = mockActivity(), onOpenDetailsPage = {
    })
}

@Preview(showBackground = true)
@Composable
fun ActivityDetailsBottomSheetContentPreview() {
    ActivityDetailsBottomSheetContent(
        activity = mockActivity(),
        onOpenDetailsPage = {},
        onShowBottomSheetChanged = { }
    )
}

