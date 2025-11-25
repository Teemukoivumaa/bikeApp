package com.example.bikeapp.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bikeapp.R
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import java.text.DecimalFormat

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProfileScreen(
    navController: NavController
) {
    val viewModel: ProfileScreenViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Profile") }) },
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else if (uiState.errorMessage != null) {
                    Text("${uiState.errorMessage}")
                    Button(onClick = {
                        navController.navigate("create_profile")
                    }) {
                        Text("Would you like to create an account?")
                    }
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                    uiState.userName?.let { name ->
                        Text(text = name, style = MaterialTheme.typography.headlineSmall)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${uiState.totalWorkouts ?: 0}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Total Workouts",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val df = remember { DecimalFormat("#.##") }
                            Text(
                                text = "${df.format(uiState.totalDistance ?: 0)} km",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(text = "Distance", style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val df = remember { DecimalFormat("#.##") }
                            Text(
                                text = "${df.format(uiState.totalElevationGain ?: 0)} m",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Elevation Gain",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Monthly Progress", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    val chartData = when (uiState.selectedChart) {
                        ChartView.DISTANCE -> uiState.monthlyDistance
                        ChartView.WORKOUTS -> uiState.monthlyWorkouts
                        ChartView.ELEVATION -> uiState.monthlyElevation
                    }

                    val chartLabel = when (uiState.selectedChart) {
                        ChartView.DISTANCE -> "Distance (KM)"
                        ChartView.WORKOUTS -> "Workouts"
                        ChartView.ELEVATION -> "Elevation (M)"
                    }

                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        SegmentedButton(
                            selected = uiState.selectedChart == ChartView.DISTANCE,
                            onClick = { viewModel.onChartSelectionChanged(ChartView.DISTANCE) },
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("Distance")
                        }
                        Spacer(modifier = Modifier.weight(0.25f))
                        SegmentedButton(
                            selected = uiState.selectedChart == ChartView.WORKOUTS,
                            onClick = { viewModel.onChartSelectionChanged(ChartView.WORKOUTS) },
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("Workouts")
                        }
                        Spacer(modifier = Modifier.weight(0.25f))
                        SegmentedButton(
                            selected = uiState.selectedChart == ChartView.ELEVATION,
                            onClick = { viewModel.onChartSelectionChanged(ChartView.ELEVATION) },
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("Elevation")
                        }
                    }

                    if (chartData.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        LineChart(
                            modifier = Modifier
                                .height(300.dp)
                                .fillMaxWidth(),
                            data = listOf(
                                Line(
                                    label = chartLabel,
                                    values = chartData,
                                    color = SolidColor(colorResource(id = R.color.vibrant_green)),
                                    dotProperties = DotProperties(
                                        enabled = true,
                                        color = SolidColor(Color.White),
                                        strokeWidth = 2.dp,
                                        radius = 2.dp,
                                        strokeColor = SolidColor(Color.DarkGray),
                                    )
                                )
                            ),
                            animationMode = AnimationMode.Together(delayBuilder = {
                                it * 500L
                            }),
                            indicatorProperties = HorizontalIndicatorProperties(
                                enabled = true,
                                textStyle = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White
                                ),
                            ),
                            labelProperties = LabelProperties(
                                enabled = true,
                                textStyle = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White
                                ),
                                labels = uiState.monthlyProgressLabels,
                                rotation = LabelProperties.Rotation(
                                    mode = LabelProperties.Rotation.Mode.Force,
                                    degree = -90f
                                )
                            ),
                            labelHelperProperties = LabelHelperProperties(
                                enabled = true,
                                textStyle = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White
                                ),
                            )
                        )
                    }
                }
            }
        }
    }
}