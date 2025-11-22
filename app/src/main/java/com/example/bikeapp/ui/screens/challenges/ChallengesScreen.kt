package com.example.bikeapp.ui.screens.challenges

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bikeapp.R
import com.example.bikeapp.data.model.ChallengeEntity
import com.example.bikeapp.utils.formatDate
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesScreen(navController: NavController) {
    val viewModel: ChallengesViewModel = hiltViewModel()

    val challenges by viewModel.challenges.collectAsState()
    val activeChallenges = challenges.filter { it.isActive && !it.isCompleted }
    val completedChallenges = challenges.filter { it.isCompleted }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Challenges") },
            )
        }
    ) { innerPadding ->
        if (challenges.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No challenges found", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Would you like to create one?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.navigate("create_challenge")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create Challenge")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section for Active Challenges
                item {
                    Text(
                        text = "Active Challenges",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (activeChallenges.isEmpty()) {
                    item {
                        Text(
                            text = "No active challenges found",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    items(activeChallenges) { challenge ->
                        ChallengeCard(challenge = challenge, navController = navController)
                    }
                }
                // Section for Completed Challenges
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Completed Challenges",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (completedChallenges.isEmpty()) {
                    item {
                        Text(
                            text = "No completed challenges found",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    items(completedChallenges) { challenge ->
                        ChallengeCard(challenge = challenge, navController = navController)
                    }
                }

                // Section to Create New Challenges
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Create New Challenges",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.navigate("create_challenge") },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Filled.PlayArrow,
                                contentDescription = "Go to Create Challenge",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Create new challenges!",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeCard(challenge: ChallengeEntity, navController: NavController) {
    val progress = if (challenge.goal > 0) (challenge.currentProgress / challenge.goal).coerceIn(
        0f,
        1f
    ) else 0f
    val progressColor =
        if (challenge.isCompleted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { navController.navigate("challengeDetails/${challenge.id}") },
        colors = CardDefaults.cardColors(containerColor = if (challenge.isCompleted) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = challenge.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (challenge.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = challenge.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${
                    String.format(
                        Locale.getDefault(),
                        "%.1f",
                        challenge.currentProgress
                    )
                } ${challenge.unit.unit} / ${
                    String.format(
                        Locale.getDefault(),
                        "%.1f",
                        challenge.goal
                    )
                } ${challenge.unit.unit}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ends: ${formatDate(challenge.endDate)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                if (challenge.isCompleted) {
                    Icon(
                        painterResource(R.drawable.challenges),
                        contentDescription = "Completed",
                        tint = MaterialTheme.colorScheme.tertiary
                    )

                } else if (!challenge.isActive) {
                    Text(
                        "Upcoming",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}