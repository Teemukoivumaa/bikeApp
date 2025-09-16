package com.example.bikeapp.ui.screens.strava

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeapp.BuildConfig
import com.example.bikeapp.data.local.AppDatabase
import com.example.bikeapp.data.local.AuthenticationStateKeys.AUTHENTICATED
import com.example.bikeapp.data.local.SecureStorageManager
import com.example.bikeapp.data.model.ActivityLocationEntity
import com.example.bikeapp.data.model.AthleteEntity
import com.example.bikeapp.data.model.StravaActivityEntity
import com.example.bikeapp.data.model.mockAthlete
import com.example.bikeapp.data.remote.ActivityResponse
import com.example.bikeapp.data.remote.RefreshTokenRequest
import com.example.bikeapp.data.remote.StravaRepository
import com.example.bikeapp.utils.calculateEndTime
import com.example.bikeapp.utils.convertStringToDateUsingTime
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@HiltViewModel
class SharedAuthViewModel @Inject constructor(
    private val database: AppDatabase,
    private val secureStorageManager: SecureStorageManager,
    private val stravaRepository: StravaRepository
) : ViewModel() {
    // Channel for one-time events like showing a toast
    private val _toastEvents = Channel<String>()
    val toastEvents = _toastEvents.receiveAsFlow() // Expose as Flow

    // Trigger to notify the data has been fetched
    private val _refreshTrigger = MutableSharedFlow<Unit>()
    val refreshTrigger: SharedFlow<Unit> = _refreshTrigger

    fun notifyDataFetched() {
        viewModelScope.launch {
            _refreshTrigger.emit(Unit)
        }
    }

    fun getRefreshToken(): String? {
        return secureStorageManager.getRefreshToken()
    }

    fun isTokenExpired(expiresAt: Long?): Boolean {
        if (expiresAt == null) {
            return true
        }

        val currentTimestampSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        return currentTimestampSeconds >= expiresAt
    }

    fun isAuthenticated(): Boolean {
        return secureStorageManager.getAuthenticationState() == AUTHENTICATED
    }

    fun refreshStravaToken(coroutineScope: CoroutineScope) {
        if (!isAuthenticated()) {
            Log.i("SharedAuthVM", "Not authenticated, no need to refresh token.")
            return
        }

        Log.i("SharedAuthVM", "Checking token expiration...")
        val expiresAt = secureStorageManager.getExpiresAt()
        val isTokenExpired = isTokenExpired(expiresAt?.toLong())
        Log.d("SharedAuthVM", "Token expired: $isTokenExpired")

        val refreshToken = getRefreshToken()

        if (!isTokenExpired || refreshToken == null) {
            val logMes = if (!isTokenExpired) "Token is not expired" else "No refresh token found"
            Log.i("SharedAuthVM", logMes)

            return
        }

        Log.i("SharedAuthVM", "Token is expired, need to refresh.")

        coroutineScope.launch {
            val refreshTokenRequest = RefreshTokenRequest(
                client_id = BuildConfig.STRAVA_CLIENT_ID,
                client_secret = BuildConfig.STRAVA_CLIENT_SECRET,
                refresh_token = refreshToken
            )
            val refreshResponse = stravaRepository.getRefreshToken(refreshTokenRequest)

            if (refreshResponse != null) {
                secureStorageManager.saveAccessToken(refreshResponse.access_token)
                secureStorageManager.saveRefreshToken(refreshResponse.refresh_token)
                secureStorageManager.saveExpiresAt(refreshResponse.expires_at)
                secureStorageManager.saveExpiresIn(refreshResponse.expires_in)

                Log.i("SharedAuthVM", "Token refreshed successfully.")
            } else {
                // Token exchange failed, need to re auth
                // secureStorageManager.deleteAccessToken()
                Log.e("SharedAuthVM", "Token refreshing failed: Failed to exchange token.")
            }
        }
    }

    fun fetchAthlete() {
        val authToken = secureStorageManager.getAccessToken()
        if (authToken == null) {
            Log.e("SharedAuthVM", "Access token is null")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val athlete = stravaRepository.getAthleteInfo(authorization = authToken)

            if (athlete != null) {
                Log.d("SharedAuthVM", "Athlete: $athlete")

                val newStravaAthlete = AthleteEntity(
                    id = athlete.id,
                    username = athlete.username,
                    firstname = athlete.firstname,
                    lastname = athlete.lastname,
                    city = athlete.city,
                    state = athlete.state,
                    country = athlete.country,
                    sex = athlete.sex,
                )

                withContext(Dispatchers.IO) {
                    database.athleteDao().insertAthlete(newStravaAthlete)
                }

            } else {
                Log.e("SharedAuthVM", "Failed to get athlete.")
            }

        }
    }

    fun fetchActivities() {
        val authToken = secureStorageManager.getAccessToken()
        if (authToken == null) {
            Log.e("StravaLoginScreen", "Access token is null")
            return
        }

        viewModelScope.launch { // Using viewModelScope for lifecycle management

            _toastEvents.send("Started fetching activities")
            val activityResponses = mutableListOf<Deferred<List<ActivityResponse>?>>()
            // Launch multiple coroutines to fetch activities concurrently
            for (i in 1..20) {
                val page = i
                activityResponses.add(async(Dispatchers.IO) {
                    stravaRepository.getAthleteActivities(
                        authorization = authToken, perPage = 30, page = page
                    )
                })
            }

            // Await all the deferred results concurrently
            val allActivities = activityResponses.awaitAll().filterNotNull().flatten()

            if (allActivities.isNotEmpty()) {
                val newActivitiesToInsert = mutableListOf<StravaActivityEntity>()
                val newLocationsToInsert = mutableListOf<ActivityLocationEntity>()

                // Prepare activities and locations for bulk insertion
                for (activity in allActivities) {
                    if (activity.type != "Ride") {
                        continue
                    }

                    val externalId = activity.id.toString()

                    // Check database on the main thread (less context switching)
                    if (database.stravaActivityDao().getActivityByExternalId(externalId) != null) {
                        continue
                    }

                    val startDate = convertStringToDateUsingTime(activity.startDate, activity.timezone)
                    val activityEndTime = calculateEndTime(startDate, activity.movingTime)
                    val newActivity = StravaActivityEntity(
                        id = 0,
                        name = activity.name,
                        startDate = startDate,
                        distance = activity.distance,
                        type = activity.type,
                        movingTime = activity.movingTime,
                        elapsedTime = activity.elapsedTime,
                        activityEndTime = activityEndTime,
                        averageSpeed = activity.averageSpeed,
                        averageHeartrate = activity.averageHeartrate,
                        maxHeartrate = activity.maxHeartrate,
                        maxSpeed = activity.maxSpeed,
                        totalElevationGain = activity.totalElevationGain,
                        averageWatts = activity.averageWatts,
                        externalId = externalId,
                        description = activity.description,
                        calories = activity.calories,
                        sportType = activity.sportType,
                        elevHigh = activity.elevHigh,
                        elevLow = activity.elevLow,
                        deviceName = activity.deviceName,
                    )
                    newActivitiesToInsert.add(newActivity)

                    // Prepare locations
                    val startLocation = activity.startLatlng
                    if (startLocation?.size == 2) {
                        newLocationsToInsert.add(
                            ActivityLocationEntity(
                                id = 0,
                                activityId = 0, // Placeholder, will be updated after activity insertion
                                latitude = startLocation[0],
                                longitude = startLocation[1],
                                coordinatesAsString = "${startLocation[0]},${startLocation[1]}",
                                type = "Start"
                            )
                        )
                    }
                    val endLocation = activity.endLatlng
                    if (endLocation?.size == 2) {
                        newLocationsToInsert.add(
                            ActivityLocationEntity(
                                id = 0,
                                activityId = 0, // Placeholder
                                latitude = endLocation[0],
                                longitude = endLocation[1],
                                coordinatesAsString = "${endLocation[0]},${endLocation[1]}",
                                type = "End"
                            )
                        )
                    }
                }

                if (newActivitiesToInsert.isNotEmpty()) {
                    withContext(Dispatchers.IO) {
                        // Bulk insert activities and get their IDs
                        val insertedActivityIds =
                            database.stravaActivityDao().insertAll(newActivitiesToInsert)

                        // Update location activity IDs
                        val updatedLocations = newLocationsToInsert.mapIndexed { index, location ->
                            location.copy(activityId = insertedActivityIds[index / 2]) // Assuming Start and End for each activity
                        }

                        // Bulk insert locations
                        database.locationDao().insertAll(updatedLocations)
                    }
                    Log.d(
                        "StravaLoginScreen",
                        "${newActivitiesToInsert.size} new activities inserted into database."
                    )
                } else {
                    Log.d("StravaLoginScreen", "No new activities to insert.")
                }

                // Send toast event to Ui and notification to refresh
                _toastEvents.send("Finished fetching activities")
                notifyDataFetched()

            } else {
                Log.e("StravaLoginScreen", "Failed to get activities.")
                _toastEvents.send("Failed to get activities")
            }
        }
    }
}

// Simulate login
private suspend fun performLogin(email: String, password: String): AthleteEntity {
    Log.d("PerformLogin", "Simulating network delay for login")
    delay(1000) // Simulate network delay

    // Simulate a potential error
     if (email.contains("error")) {
         throw Exception("Invalid credentials simulated")
     }

    Log.d("PerformLogin", "Network delay finished, returning mock athlete")
    return mockAthlete() // Simplified
}



