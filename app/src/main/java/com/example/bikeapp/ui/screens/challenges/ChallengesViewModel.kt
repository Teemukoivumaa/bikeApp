package com.example.bikeapp.ui.screens.challenges

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.bikeapp.data.local.AppDatabase
import com.example.bikeapp.data.model.ChallengeEntity
import com.example.bikeapp.data.model.StravaActivityEntity
import com.example.bikeapp.worker.ChallengeWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    database: AppDatabase,
    private val application: Application
) : ViewModel() {

    private val challengesDao = database.challengeDao()
    private val stravaActivityDao = database.stravaActivityDao()

    val challenges: StateFlow<List<ChallengeEntity>> =
        challengesDao.getAllChallenges()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val activities: StateFlow<List<StravaActivityEntity>> =
        stravaActivityDao.getAllActivitiesSortedByDate()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        observeChallengesAndActivities()
    }

    suspend fun getChallenge(challengeId: Int): ChallengeEntity? {
        return challengesDao.getChallengeById(challengeId)
    }

    fun deleteChallenge(challengeId: Int?) {
        if (challengeId == null) return

        viewModelScope.launch {
            challengesDao.deleteChallengeById(challengeId)
        }
    }

    private fun observeChallengesAndActivities() {
        viewModelScope.launch {
            combine(challenges, activities) { challengesList, activitiesList ->
                if (challengesList.isNotEmpty() && activitiesList.isNotEmpty()) {
                    calculateAndSaveChanges(challengesList, activitiesList)
                }
            }.collect() // Terminal operator to start the flow
        }
    }

    private fun calculateAndSaveChanges(
        challengesList: List<ChallengeEntity>,
        activitiesList: List<StravaActivityEntity>
    ) {
        viewModelScope.launch {
            challengesList.forEach { challenge ->
                val relevantActivities = activitiesList.filter { activity ->
                    activity.startDate >= challenge.startDate && activity.startDate <= challenge.endDate
                }

                val totalDistanceForChallengeMeters =
                    relevantActivities.sumOf { it.distance.toDouble() }

                val totalDistanceForChallenge = when (challenge.unit.unit) {
                    "km" -> totalDistanceForChallengeMeters / 1000
                    else -> totalDistanceForChallengeMeters
                }

                val updatedProgress = totalDistanceForChallenge.toFloat()
                val isCompleted = updatedProgress >= challenge.goal
                val wasCompleted = challenge.isCompleted

                if (challenge.currentProgress != updatedProgress || wasCompleted != isCompleted) {
                    val updatedChallenge = challenge.copy(
                        currentProgress = updatedProgress,
                        isCompleted = isCompleted
                    )
                    challengesDao.update(updatedChallenge)
                    Log.d(
                        "ChallengesViewModel",
                        "Challenge ${challenge.id} updated. Progress: $updatedProgress, Completed: $isCompleted"
                    )

                    // If the challenge was just completed, schedule the next one
                    if (!wasCompleted && isCompleted) {
                        scheduleNextChallenge(updatedChallenge)
                    }
                }
            }
        }
    }

    fun scheduleNextChallenge(challenge: ChallengeEntity) {
        // Only schedule if the challenge is recurring
        if (challenge.recurring == null) return

        val workManager = WorkManager.getInstance(application)

        // Pass the entire challenge entity to the worker to avoid race conditions.
        val workRequest = ChallengeWorker.oneTimeWorkRequest(challenge)

        workManager.enqueueUniqueWork(
            "challenge_worker_${challenge.id}",
            androidx.work.ExistingWorkPolicy.REPLACE,
            workRequest
        )
        Log.d("ChallengesViewModel", "Scheduled next challenge for challenge ID: ${challenge.id}")
    }
}