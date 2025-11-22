package com.example.bikeapp.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    navController: NavController,
) {
    val viewModel: CreateAccountViewModel = hiltViewModel()
    // Observe the UI state from the ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // If the form is successfully saved, navigate back
    if (uiState.isSuccess) {
        navController.navigate("profile_screen")
    }

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
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.username,
                    onValueChange = { viewModel.onUsernameChange(it) },
                    label = { Text("Username") },
                    isError = uiState.usernameError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (uiState.usernameError != null) {
                    Text(
                        text = uiState.usernameError!!,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.firstName,
                    onValueChange = { viewModel.onFirstNameChange(it) },
                    label = { Text("First Name") },
                    isError = uiState.firstNameError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (uiState.firstNameError != null) {
                    Text(
                        text = uiState.firstNameError!!,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.lastName,
                    onValueChange = { viewModel.onLastNameChange(it) },
                    label = { Text("Last Name") },
                    isError = uiState.lastNameError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (uiState.lastNameError != null) {
                    Text(
                        text = uiState.lastNameError!!,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.saveAthlete() },
                    enabled = uiState.isFormValid, // Button is enabled only if the form is valid
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Athlete")
                }
            }
        }
    }
}



