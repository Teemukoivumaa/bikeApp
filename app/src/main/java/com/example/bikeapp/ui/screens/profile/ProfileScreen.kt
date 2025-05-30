package com.example.bikeapp.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProfileScreen() {

    val viewModel: ProfileScreenViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Profile") }) },
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else if (uiState.errorMessage != null) {
                    Text("Error: ${uiState.errorMessage}")
                    Button(onClick = { viewModel.clearErrorMessage() }) {
                        Text("Retry") // Or just "OK"
                    }
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                    uiState.userName?.let { name ->
                        Text(text = name, style = MaterialTheme.typography.headlineSmall)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    uiState.totalDistance?.let { distance ->
                        Text(text = "Total Distance: $distance km", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}