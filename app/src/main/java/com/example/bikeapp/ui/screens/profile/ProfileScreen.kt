package com.example.bikeapp.ui.screens.profile

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line

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
                    uiState.totalDistance?.let { distance ->
                        Text(
                            text = "Total Distance: $distance km",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
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
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        // First Stat
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "25", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = "Total Workouts",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        // Second Stat
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "150 km", style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Distance", style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        // Third Stat
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "12", style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Bikes Owned", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Monthly Progress", style = MaterialTheme.typography.bodySmall)
                    // This is a placeholder for your chart

                    LineChart(
                        modifier = Modifier
                            .height(300.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp),
                        data = remember {
                            listOf(
                                Line(
                                    label = "Windows",
                                    values = listOf(28.0, 41.0, 5.0, 10.0, 35.0),
                                    color = SolidColor(Color(0xFF23af92)),
                                    firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                                    secondGradientFillColor = Color.Transparent,
                                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                                    gradientAnimationDelay = 1000,
                                    drawStyle = DrawStyle.Stroke(width = 2.dp),
                                )
                            )
                        },
                        animationMode = AnimationMode.Together(delayBuilder = {
                            it * 500L
                        }),
                    )

                }
            }
        }
    }
}
