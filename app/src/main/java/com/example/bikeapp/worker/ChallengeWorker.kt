package com.example.bikeapp.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import com.example.bikeapp.data.local.AppDatabase
import com.example.bikeapp.data.model.ChallengeEntity
import com.example.bikeapp.data.model.ChallengeUnit
import java.time.ZoneId
import java.util.Date

class ChallengeWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "ChallengeWorker"
        // Keys for passing data to the worker
        const val KEY_RECURRING = "recurring"
        const val KEY_NAME = "name"
        const val KEY_DESCRIPTION = "description"
        const val KEY_GOAL = "goal"
        const val KEY_UNIT = "unit"
        const val KEY_END_DATE = "end_date"


        fun oneTimeWorkRequest(challenge: ChallengeEntity): OneTimeWorkRequest {
            val inputData = Data.Builder()
                .putString(KEY_NAME, challenge.name)
                .putString(KEY_DESCRIPTION, challenge.description)
                .putFloat(KEY_GOAL, challenge.goal)
                .putString(KEY_UNIT, challenge.unit.unit)
                .putLong(KEY_END_DATE, challenge.endDate.time)

            // Only add recurring if it exists
            challenge.recurring?.let {
                inputData.putString(KEY_RECURRING, it)
            }

            return OneTimeWorkRequest.Builder(ChallengeWorker::class.java)
                .setInputData(inputData.build())
                .addTag(TAG)
                .build()
        }
    }

    override suspend fun doWork(): Result {
        val challengesDao = AppDatabase.getInstance(applicationContext).challengeDao()

        // Retrieve all challenge data directly from input.
        val recurring = inputData.getString(KEY_RECURRING)
        val name = inputData.getString(KEY_NAME)!!
        val description = inputData.getString(KEY_DESCRIPTION)
        val goal = inputData.getFloat(KEY_GOAL, 0f)
        val unit = inputData.getString(KEY_UNIT)!!
        val endDate = Date(inputData.getLong(KEY_END_DATE, 0L))

        // If the challenge is not recurring, there's nothing more to do
        if (recurring == null) {
            Log.d(TAG, "Challenge is not recurring. No new challenge will be created.")
            return Result.success()
        }

        // Create a temporary challenge object from the input data to perform calculations.
        val completedChallenge = ChallengeEntity(
            id = 0, // Not needed for calculation
            name = name,
            description = description ?: "",
            goal = goal,
            unit = ChallengeUnit.entries.first { it.unit.equals(unit, ignoreCase = true) },
            startDate = Date(), // Not needed for calculation
            endDate = endDate,
            recurring = recurring,
            isCompleted = true,
            isActive = false,
            currentProgress = 0f,
        )

        // Calculate the new start and end dates for the next challenge
        val (newStartDate, newEndDate) = calculateNextChallengeDates(completedChallenge)

        // Create a new challenge entity with the updated dates and reset progress
        val newChallenge = completedChallenge.copy(
            id = 0, // Set to 0 to auto-generate a new primary key
            startDate = newStartDate,
            endDate = newEndDate,
            currentProgress = 0f, // Reset the progress
            isCompleted = false, // The new challenge is not completed
            isActive = true // The new challenge is active
        )

        Log.d(TAG, "New Challenge: $newChallenge")


        // Insert the new challenge into the database
        val newChallengeId = challengesDao.insert(newChallenge)

        Log.d(TAG, "Successfully created new recurring challenge with ID: $newChallengeId")

        // Indicate that the work was completed successfully
        return Result.success()
    }

    // Calculates the start and end dates for the next challenge based on the recurrence rule
    private fun calculateNextChallengeDates(challenge: ChallengeEntity): Pair<Date, Date> {

        // 1. Convert old java.util.Date to modern LocalDate
        val previousEndDate = challenge.endDate
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        // 2. Calculate New Start Date (Always the next day)
        val newStartDate = previousEndDate.plusDays(1)

        // 3. Calculate New End Date based on Recurrence
        val newEndLocalDate = when (challenge.recurring) {
            "Weekly" -> newStartDate.plusWeeks(1).minusDays(1)
            "Monthly" -> newStartDate.plusMonths(1).minusDays(1)
            "Yearly" -> newStartDate.plusYears(1).minusDays(1)
            else -> throw IllegalArgumentException("Unknown recurrence type")
        }

        // Example: If newStartDate is Jan 1, newEndLocalDate is Jan 31 (plus 1 month, minus 1 day).

        // 4. Convert back to the old java.util.Date object for return (if necessary)
        val newStartFinalDate = Date.from(newStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val newEndFinalDate = Date.from(newEndLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        return Pair(newStartFinalDate, newEndFinalDate)
    }
}