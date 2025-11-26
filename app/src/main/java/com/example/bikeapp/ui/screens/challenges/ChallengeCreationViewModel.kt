package com.example.bikeapp.ui.screens.challenges

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeapp.data.local.AppDatabase
import com.example.bikeapp.data.model.ChallengeEntity
import com.example.bikeapp.data.model.ChallengeUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit


data class ChallengeFormState(
    val name: String = "",
    val description: String? = "",
    val goalInput: String = "0.0",
    val goal: Double = 0.0,
    val unit: String = "km",
    val startDate: Long? = System.currentTimeMillis(),
    val endDate: Long? = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7),
    val recurring: Boolean = false,
    val recurringInterval: String? = null,
    val nameError: String? = null,
    val goalError: String? = null,
    val startDateError: String? = null,
    val endDateError: String? = null,
    val isFormValid: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

val recurringChallengeOptions = listOf("Weekly", "Monthly", "Yearly")
val challengeUnits = listOf("km", "miles", "hours", "minutes", "m ascent", "ft ascent", "rides", "days", "kcal")

@HiltViewModel
class ChallengeCreationViewModel @Inject constructor(
    private val database: AppDatabase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChallengeFormState())

    // Public read-only state flow that the UI will observe
    val uiState: StateFlow<ChallengeFormState> = _uiState.asStateFlow()

    private fun validateForm(state: ChallengeFormState) {
        val isNameValid = state.name.isNotBlank()
        val isDescriptionValid = state.description?.isNotBlank()
        val isGoalValid = state.goal > 0

        val startDate = Date(state.startDate!!)
        val endDate = Date(state.endDate!!)

        val isStartDateValid = startDate.before(endDate)
        val isEndDateValid = endDate.after(startDate)

        _uiState.update { currentState ->
            currentState.copy(
                nameError = if (isNameValid) null else "Name cannot be empty",
                goalError = if (isGoalValid) null else "Goal must be greater than 0",
                startDateError = if (isStartDateValid) null else "Start date must be before end date",
                endDateError = if (isEndDateValid == true) null else "End date must be after start date",
                isFormValid = isNameValid && isDescriptionValid == true && isGoalValid && isStartDateValid && isEndDateValid == true
            )
        }
    }

    fun onNameChange(newValue: String) {
        _uiState.update { currentState ->
            val newState = currentState.copy(name = newValue)
            validateForm(newState)
            newState
        }
    }

    fun onDescriptionChange(newValue: String) {
        _uiState.update { currentState ->
            val newState = currentState.copy(description = newValue)
            validateForm(newState)
            newState
        }
    }

    fun onGoalChange(newValue: String) {
        _uiState.update { currentState ->
            val goalInputValue = newValue
            val parsedGoal = newValue.toDoubleOrNull()

            val newState = if (parsedGoal != null) {
                currentState.copy(
                    goalInput = goalInputValue,
                    goal = parsedGoal,
                )
            } else { // Handle invalid input
                currentState.copy(
                    goalInput = goalInputValue,
                    goal = 0.0, // Or keep currentState.goal if you prefer
                    goalError = if (goalInputValue.isNotEmpty()) "Invalid number format" else null,
                    isFormValid = false
                )
            }

            if (parsedGoal != null) { // Only validate if it's a valid number
                validateForm(newState)
            }
            newState
        }
    }

    fun onChallengeUnitChange(newValue: String) {
        _uiState.update { currentState ->
            val newState = currentState.copy(unit = newValue)
            validateForm(newState)
            newState
        }
    }

    fun onStartDateChange(newValue: Long) {
        _uiState.update { currentState ->
            val newState = currentState.copy(startDate = newValue)
            validateForm(newState)
            newState
        }
    }

    fun onEndDateChange(newValue: Long) {
        _uiState.update { currentState ->
            val newState = currentState.copy(endDate = newValue)
            validateForm(newState)
            newState
        }
    }

    fun onRecurringChange(newValue: Boolean) {
        _uiState.update { currentState ->
            val newState = currentState.copy(recurring = newValue)
            validateForm(newState)
            newState
        }
    }

    fun onRecurringIntervalChange(newValue: String) {
        _uiState.update { currentState ->
            val newState = currentState.copy(recurringInterval = newValue)
            validateForm(newState)
            newState
        }
    }

    fun saveChallenge() {
        if (!uiState.value.isFormValid) {
            _uiState.update { currentState ->
                currentState.copy(error = "Form is not valid")
            }
            return
        }

        val challenge = ChallengeEntity(
            id = 0,
            name = uiState.value.name,
            description = uiState.value.description ?: "",
            goal = uiState.value.goal.toFloat(),
            startDate = Date(uiState.value.startDate?: 0),
            endDate = Date(uiState.value.endDate?: 0),
            currentProgress = 0f,
            unit = ChallengeUnit.entries.first { it.unit.equals(uiState.value.unit, ignoreCase = true) },
            isActive = true,
            isCompleted = false,
            recurring = uiState.value.recurringInterval
        )

        Log.d("ChallengeCreationViewModel", "Saving new challenge: $challenge")

        viewModelScope.launch {
            try {
                database.challengeDao().insert(challenge)

                Log.d("ChallengeCreationViewModel", "Challenge saved successfully")

                _uiState.update { currentState ->
                    currentState.copy(isSuccess = true, error = null)
                }
            } catch (e: Exception) {
                Log.e("ChallengeCreationViewModel", "Error saving new challenge", e)

                _uiState.update { currentState ->
                    currentState.copy(isSuccess = false, error = "Error saving challenge")
                }
            }
        }

    }

}