package com.example.bikeapp.ui.screens.challenges

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bikeapp.R
import com.example.bikeapp.data.model.ChallengeEntity
import com.example.bikeapp.ui.components.ProgressCard
import com.example.bikeapp.utils.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeDetailsScreen(
    navController: NavController,
    challengeId: Int,
    viewModel: ChallengesViewModel = hiltViewModel()
) {
    var challenge by remember { mutableStateOf<ChallengeEntity?>(null) }

    LaunchedEffect(challengeId) {
        challenge = viewModel.getChallenge(challengeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = challenge?.name ?: "Challenge Details") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Implement editing */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Challenge")
                    }
                    IconButton(onClick = {
                        viewModel.deleteChallenge(challenge?.id)
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Challenge",
                            tint = MaterialTheme.colorScheme.errorContainer
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (challenge == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading Challenge...")
                }
            } else {
                val currentChallenge = challenge!!
                val progress =
                    if (currentChallenge.goal > 0) (currentChallenge.currentProgress / currentChallenge.goal).coerceIn(
                        0f,
                        1f
                    ) else 0f
                val progressColor =
                    if (currentChallenge.isCompleted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary

                Text(currentChallenge.description, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(24.dp))

                ProgressCard(
                    title = "Progress",
                    progress = progress,
                    progressColor = progressColor,
                    currentProgress = currentChallenge.currentProgress,
                    goal = currentChallenge.goal,
                    unit = currentChallenge.unit.unit
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Starts",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            formatDate(currentChallenge.startDate),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Ends",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            formatDate(currentChallenge.endDate),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                currentChallenge.recurring?.let {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Repeats: $it", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.weight(1f))
                        Button(onClick = {viewModel.scheduleNextChallenge(currentChallenge)}) {
                            Text("Schedule Next Challenge")
                        }
                    }
                }
            }
        }
    }
}