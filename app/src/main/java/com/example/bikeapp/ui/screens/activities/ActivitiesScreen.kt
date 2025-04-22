package com.example.bikeapp.ui.screens.activities

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.bikeapp.R
import com.example.bikeapp.data.model.StravaActivityEntity
import com.example.bikeapp.ui.components.StatisticCard
import com.example.bikeapp.ui.components.StatisticItemWithIcon
import com.example.bikeapp.utils.convertMsToKmh
import com.example.bikeapp.utils.convertMtoKm
import com.example.bikeapp.utils.formatDate
import com.example.bikeapp.utils.formatDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(viewModel: ActivityViewModel, navController: NavHostController) {
    val activities by viewModel.activities.collectAsState()
    val totalLength by viewModel.totalLength.collectAsState()

    val listState = rememberLazyListState()
    var showStatsCards by rememberSaveable { mutableStateOf(true) }

    val firstVisibleItemScrollOffset by remember {
        derivedStateOf { listState.firstVisibleItemScrollOffset }
    }

    LaunchedEffect(firstVisibleItemScrollOffset) {
        showStatsCards = firstVisibleItemScrollOffset == 0
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
        topBar = { TopAppBar(title = { Text("Activities") }) },
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
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
                            onDelete = { viewModel.deleteActivity(activity) },
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
fun ActivityCard(
    activity: StravaActivityEntity,
    onDelete: () -> Unit,
    onOpenDetailsPage: (StravaActivityEntity) -> Unit
) {
    ActivityCardContent(
        activity = activity,
        onDelete = onDelete,
        onOpenDetailsPage = onOpenDetailsPage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivityCardContent(
    activity: StravaActivityEntity,
    onDelete: () -> Unit,
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
                onDelete = onDelete,
                onOpenDetailsPage = onOpenDetailsPage,
                onShowBottomSheetChanged = onShowBottomSheetChanged
            )
        }
    }
}


@Composable
fun ActivityDetailsBottomSheetContent(
    activity: StravaActivityEntity,
    onDelete: () -> Unit,
    onOpenDetailsPage: (StravaActivityEntity) -> Unit,
    onShowBottomSheetChanged: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
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
                        modifier = Modifier.size(24.dp).weight(1f)
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            StatisticItemWithIcon(
                icon = R.drawable.distance,
                text = convertMtoKm(activity.distance),
                modifier = Modifier.weight(1f),
                tooltipText = "Distance cycled"
            )
            StatisticItemWithIcon(
                icon = R.drawable.time,
                text = formatDuration(activity.elapsedTime),
                modifier = Modifier.weight(1f),
                tooltipText = "Time cycled"
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            StatisticItemWithIcon(
                icon = R.drawable.altitude,
                text = "${activity.totalElevationGain} m",
                modifier = Modifier.weight(1f),
                tooltipText = "Total elevation gain"
            )
            StatisticItemWithIcon(
                icon = R.drawable.avg_speed,
                text = "${convertMsToKmh(activity.averageSpeed)} km/h",
                modifier = Modifier.weight(1f),
                tooltipText = "Average speed"
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            StatisticItemWithIcon(
                icon = R.drawable.max_speed,
                text = "${convertMsToKmh(activity.maxSpeed)} km/h",
                modifier = Modifier.weight(1f),
                tooltipText = "Max speed"
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            StatisticItemWithIcon(
                icon = R.drawable.max_heartrate,
                text = "${activity.maxHeartrate} bpm",
                modifier = Modifier.weight(1f),
                tooltipText = "Max heartrate"
            )
            StatisticItemWithIcon(
                icon = R.drawable.avg_heartrate,
                text = "${activity.averageHeartrate} bpm",
                modifier = Modifier.weight(1f),
                tooltipText = "Average heartrate"
            )
        }
    }
}

val mockActivity = StravaActivityEntity(
    id = 1,
    name = "Sample Activity",
    type = "Ride",
    distance = 28099F,
    movingTime = 60,
    elapsedTime = 60,
    startDate = java.util.Date(),
    activityEndTime = "12:00",
    averageSpeed = 10.0f,
    averageHeartrate = 10.0f,
    maxHeartrate = 20.0f,
    maxSpeed = 10.0f,
    totalElevationGain = 10.0f,
    averageWatts = 10.0f,
    externalId = "externalId",
    description = "desc",
    calories = 200F,
    sportType = "sport",
    elevHigh = 100F,
    elevLow = 10F,
    deviceName = "Mock",
)

@Preview(showBackground = true)
@Composable
fun ActivityCardPreview() {
    ActivityCard(activity = mockActivity, onDelete = { }, onOpenDetailsPage = {

    })
}

@Preview(showBackground = true)
@Composable
fun ActivityDetailsBottomSheetContentPreview() {
    ActivityDetailsBottomSheetContent(
        activity = mockActivity,
        onDelete = {},
        onOpenDetailsPage = {},
        onShowBottomSheetChanged = { }
    )
}

