package com.example.bikeapp.ui.screens.challenges

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bikeapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeCreationScreen(navController: NavController) {
    val viewModel: ChallengeCreationViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var startDateSelection by remember { mutableStateOf(true) }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    // State for controlling the visibility of the DatePickerDialog
    val openDatePickerDialog = remember { mutableStateOf(false) }
    var expandedRecurringChallenge by remember { mutableStateOf(false) }
    var expandedChallengeUnit by remember { mutableStateOf(false) }

    if (uiState.isSuccess) {
        navController.navigate("challenges")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Challenges") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Navigate back or open drawer */ }) {
                        Icon(
                            painterResource(R.drawable.challenges),
                            contentDescription = "Challenges Icon"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Create new challenge",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.onNameChange(it) },
                    label = { Text("Challenge Name") },
                    isError = uiState.nameError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (uiState.nameError != null) {
                    Text(
                        text = uiState.nameError!!,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.description.toString(),
                    onValueChange = { viewModel.onDescriptionChange(it) },
                    label = { Text("Description (Optional)") },
                    isError = uiState.descriptionError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (uiState.descriptionError != null) {
                    Text(
                        text = uiState.descriptionError!!,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.goalInput,
                    onValueChange = {
                        viewModel.onGoalChange(it)
                    },
                    label = { Text("Challenge") },
                    isError = uiState.goalError != null,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                if (uiState.goalError != null) {
                    Text(
                        text = uiState.goalError!!,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedChallengeUnit,
                    onExpandedChange = { expandedChallengeUnit = !expandedChallengeUnit },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = uiState.unit,
                        onValueChange = { /* Read-only */ },
                        readOnly = true,
                        label = { Text("Challenge Unit") },
                        trailingIcon = {
                            Icon(
                                imageVector = if (expandedChallengeUnit) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expand dropdown"
                            )
                        },
                        modifier = Modifier
                            .menuAnchor(
                                MenuAnchorType.PrimaryNotEditable,
                                true
                            ) // This is crucial for the dropdown to anchor to the TextField
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedChallengeUnit,
                        onDismissRequest = { expandedChallengeUnit = false }
                    ) {
                        challengeUnits.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    viewModel.onChallengeUnitChange(option)
                                    expandedChallengeUnit = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clickable {
                            openDatePickerDialog.value = true
                            startDateSelection = true
                        }
                ) {
                    OutlinedTextField(
                        value = uiState.startDate.let { millis ->
                            dateFormatter.format(Date(millis ?: System.currentTimeMillis()))
                        } ?: "",
                        onValueChange = { /* Read-only */ },
                        label = { Text("Challenge Start Date (Optional)") },
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                openDatePickerDialog.value = true
                                startDateSelection = true
                            },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select Date",
                                modifier = Modifier.clickable {
                                    openDatePickerDialog.value = true
                                    startDateSelection = true
                                }
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors()
                            .copy( // Preserve enabled appearance
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledIndicatorColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                // Set disabled colors to match enabled colors if you want no visual difference
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                    )
                    if (uiState.startDateError != null) {
                        Text(
                            text = uiState.startDateError!!,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .toggleable(
                            value = uiState.recurring,
                            onValueChange = { viewModel.onRecurringChange(it) },
                            role = Role.Checkbox
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.recurring,
                        onCheckedChange = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Recurring Challenge?")
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (uiState.recurring) {
                    ExposedDropdownMenuBox(
                        expanded = expandedRecurringChallenge,
                        onExpandedChange = {
                            expandedRecurringChallenge = !expandedRecurringChallenge
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.recurringInterval ?: "",
                            onValueChange = { /* Read-only */ },
                            readOnly = true,
                            label = { Text("Recurring Challenge Interval") },
                            trailingIcon = {
                                Icon(
                                    imageVector = if (expandedRecurringChallenge) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expand dropdown"
                                )
                            },
                            modifier = Modifier
                                .menuAnchor(
                                    MenuAnchorType.PrimaryNotEditable,
                                    true
                                ) // This is crucial for the dropdown to anchor to the TextField
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedRecurringChallenge,
                            onDismissRequest = { expandedRecurringChallenge = false }
                        ) {
                            recurringChallengeOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        viewModel.onRecurringIntervalChange(option)
                                        expandedRecurringChallenge = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                openDatePickerDialog.value = true
                                startDateSelection = false
                            }
                    ) {
                        OutlinedTextField(
                            value = uiState.endDate.let { millis ->
                                dateFormatter.format(Date(millis ?: System.currentTimeMillis()))
                            } ?: "",
                            onValueChange = { /* Read-only */ },
                            label = { Text("Challenge End Date (Optional)") },
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    openDatePickerDialog.value = true
                                    startDateSelection = false
                                },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select Date",
                                    modifier = Modifier.clickable {
                                        openDatePickerDialog.value =
                                            true
                                        startDateSelection = false
                                    }
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors()
                                .copy( // Preserve enabled appearance
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledIndicatorColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    // Set disabled colors to match enabled colors if you want no visual difference
                                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        )
                        if (uiState.endDateError != null) {
                            Text(
                                text = uiState.endDateError!!,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.saveChallenge() },
                    enabled = uiState.isFormValid, // Button is enabled only if the form is valid
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Challenge")
                }

                if (openDatePickerDialog.value) {
                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = if (startDateSelection) uiState.startDate else uiState.endDate
                    )
                    val confirmEnabled = remember {
                        derivedStateOf { datePickerState.selectedDateMillis != null }
                    }

                    DatePickerDialog(
                        onDismissRequest = {
                            // Dismiss the dialog when the user clicks outside or on the back button.
                            openDatePickerDialog.value = false
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    openDatePickerDialog.value = false
                                    // Save the selected date
                                    val newSelectedDateMillis = datePickerState.selectedDateMillis
                                    if (startDateSelection) {
                                        viewModel.onStartDateChange(
                                            newSelectedDateMillis ?: System.currentTimeMillis()
                                        )
                                    } else {
                                        viewModel.onEndDateChange(
                                            newSelectedDateMillis ?: System.currentTimeMillis()
                                        )
                                    }
                                },
                                enabled = confirmEnabled.value
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    openDatePickerDialog.value = false
                                }
                            ) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState, title = {
                            Text(
                                text = if (startDateSelection) "Select Start Date" else "Select End Date",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp)
                            )
                        })
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

            }
        }
    }
}