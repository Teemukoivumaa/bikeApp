package com.example.bikeapp.ui.screens.strava

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeapp.BuildConfig
import com.example.bikeapp.data.local.AppDatabase
import com.example.bikeapp.data.local.SecureStorageManager
import com.example.bikeapp.data.model.ActivityLocationEntity
import com.example.bikeapp.data.model.AthleteEntity
import com.example.bikeapp.data.model.StravaActivityEntity
import com.example.bikeapp.data.remote.ActivityResponse
import com.example.bikeapp.data.remote.RefreshTokenRequest
import com.example.bikeapp.data.remote.StravaRepository
import com.example.bikeapp.data.remote.TokenRequest
import com.example.bikeapp.utils.calculateEndTime
import com.example.bikeapp.utils.convertStringToDateUsingTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class StravaLoginViewModel(
    private val database: AppDatabase,
    private val secureStorageManager: SecureStorageManager,
    private val stravaRepository: StravaRepository
) : ViewModel() {

    fun getAccessToken(): String? {
        return secureStorageManager.getAccessToken()
    }

    fun getRefreshToken(): String? {
        return secureStorageManager.getRefreshToken()
    }

    /**
     * This function creates an implicit intent to redirect the user to the Strava authorization endpoint.
     *
     * @param context The application context.
     */
    fun launchStravaAuthorization(context: Context) {
        val clientId = BuildConfig.STRAVA_CLIENT_ID
        val redirectUri = BuildConfig.STRAVA_REDIRECT_URI
        val intentUri = "https://www.strava.com/oauth/mobile/authorize".toUri().buildUpon()
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("redirect_uri", redirectUri)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("approval_prompt", "auto")
            .appendQueryParameter("scope", "activity:read").build()

        Log.d("StravaAuth", "Redirect URI: $redirectUri")
        Log.d("StravaAuth", "Intent URI: $intentUri")

        val intent = Intent(Intent.ACTION_VIEW, intentUri)

        // Verify that the intent will resolve to an activity
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Handle the case where no activity can handle the intent (e.g., Strava app not installed)
            Log.e("StravaLogin", "No activity found to handle Strava authorization intent.")
            // You might want to display an error message to the user here.
        }
    }


    fun exchangeToken(
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            val code = secureStorageManager.getAccessToken()
            Log.d("StravaLoginScreen", "Code: $code")
            if (code != null) {
                val tokenRequest = TokenRequest(
                    client_id = BuildConfig.STRAVA_CLIENT_ID,
                    client_secret = BuildConfig.STRAVA_CLIENT_SECRET,
                    code = code
                )
                val tokenResponse = stravaRepository.exchangeToken(tokenRequest)
                if (tokenResponse != null) {
                    secureStorageManager.saveAccessToken(tokenResponse.access_token)
                    secureStorageManager.saveRefreshToken(tokenResponse.refresh_token)
                    secureStorageManager.saveAthleteId(tokenResponse.athlete.id)
                    secureStorageManager.saveExpiresAt(tokenResponse.expires_at)
                    secureStorageManager.saveExpiresIn(tokenResponse.expires_in)

                    Log.d("StravaLoginScreen", "Access Token: ${tokenResponse.access_token}")
                    Log.d("StravaLoginScreen", "Refresh Token: ${tokenResponse.refresh_token}")
                    Log.d("StravaLoginScreen", "Expires At: ${tokenResponse.expires_at}")
                    Log.d("StravaLoginScreen", "Expires In: ${tokenResponse.expires_in}")
                    Log.d("StravaLoginScreen", "Athlete: ${tokenResponse.athlete}")
                } else {
                    // Token exchange failed, need to re auth
                    secureStorageManager.deleteAccessToken()
                    Log.e("StravaLoginScreen", "Failed to exchange token.")
                }
            } else {
                Log.e("StravaLoginScreen", "Code is null")
            }
        }
    }

    fun refreshStravaToken(coroutineScope: CoroutineScope) {

        val refreshToken = getRefreshToken()

        coroutineScope.launch {
            if (refreshToken != null) {
                val refreshTokenRequest = RefreshTokenRequest(
                    client_id = BuildConfig.STRAVA_CLIENT_ID,
                    client_secret = BuildConfig.STRAVA_CLIENT_SECRET,
                    refresh_token = refreshToken.toString()
                )
                val refreshResponse = stravaRepository.getRefreshToken(refreshTokenRequest)
                if (refreshResponse != null) {
                    secureStorageManager.saveAccessToken(refreshResponse.access_token)
                    secureStorageManager.saveRefreshToken(refreshResponse.refresh_token)
                    secureStorageManager.saveExpiresAt(refreshResponse.expires_at)
                    secureStorageManager.saveExpiresIn(refreshResponse.expires_in)

                    Log.d("StravaLoginScreen", "Access Token: ${refreshResponse.access_token}")
                    Log.d("StravaLoginScreen", "Refresh Token: ${refreshResponse.refresh_token}")
                    Log.d("StravaLoginScreen", "Expires At: ${refreshResponse.expires_at}")
                    Log.d("StravaLoginScreen", "Expires In: ${refreshResponse.expires_in}")
                } else {
                    // Token exchange failed, need to re auth
                    // secureStorageManager.deleteAccessToken()
                    Log.e("StravaLoginScreen", "Failed to exchange token.")
                }
            } else {
                Log.e("StravaLoginScreen", "Code is null")
            }
        }

    }

    fun fetchActivities(
    ) {
        val authToken = secureStorageManager.getAccessToken()
        if (authToken == null) {
            Log.e("StravaLoginScreen", "Access token is null")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {

            val allActivities = mutableListOf<ActivityResponse>()
            for (i in 1..20) {
                val page = i

                val activities = stravaRepository.getAthleteActivities(
                    authorization = authToken, perPage = 30, page = page
                )

                if (activities != null) {
                    allActivities.addAll(activities)
                } else {
                    // No more activities to fetch
                    Log.d("StravaLoginScreen", "No more activities to fetch.")
                    break
                }
            }

            if (allActivities.isNotEmpty()) {
                for (activity in allActivities) {
                    val type = activity.type
                    val externalId = activity.id.toString()

                    // If the activity is not a ride, skip it
                    if (type != "Ride") {
                        continue
                    }

                    // Check database first if it contains the activity based on the externalId
                    val existingActivity = withContext(Dispatchers.IO) {
                        database.stravaActivityDao().getActivityByExternalId(externalId)
                    }

                    if (existingActivity != null) {
                        continue
                    }

                    val startDate = convertStringToDateUsingTime(activity.startDateLocal)
                    val activityEndTime = calculateEndTime(startDate, activity.movingTime)
                    val newActivity = StravaActivityEntity(
                        id = 0, // Assuming auto-increment in the database
                        name = activity.name,
                        startDate = startDate,
                        distance = activity.distance,
                        type = type,
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

                    withContext(Dispatchers.IO) {
                        val activityId = database.stravaActivityDao().insert(newActivity)

                        val startLocation = activity.startLatlng
                        Log.d("StravaLoginScreen", "Start location: $startLocation")
                        if (startLocation?.size == 2) {
                            val activityLocation = ActivityLocationEntity(
                                id = 0,
                                activityId = activityId,
                                latitude = startLocation[0],
                                longitude = startLocation[1],
                                coordinatesAsString = "${startLocation[0]},${startLocation[1]}",
                                type = "Start"
                            )
                            database.locationDao().insert(activityLocation)
                        }
                        val endLocation = activity.endLatlng
                        if (endLocation?.size == 2) {
                            val activityLocation = ActivityLocationEntity(
                                id = 0,
                                activityId = activityId,
                                latitude = endLocation[0],
                                longitude = endLocation[1],
                                coordinatesAsString = "${endLocation[0]},${endLocation[1]}",
                                type = "End"
                            )
                            database.locationDao().insert(activityLocation)
                        }
                    }
                }

                Log.d("StravaLoginScreen", "All activities inserted into database.")

            } else {
                Log.e("StravaLoginScreen", "Failed to get activities.")
            }
        }
    }

    fun fetchAthlete() {
        val authToken = secureStorageManager.getAccessToken()
        if (authToken == null) {
            Log.e("StravaLoginScreen", "Access token is null")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val athlete = stravaRepository.getAthleteInfo(authorization = authToken)

            if (athlete != null) {
                Log.d("StravaLoginScreen", "Athlete: $athlete")

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
                    database.stravaAthleteDao().insertAthlete(newStravaAthlete)
                }

            } else {
                Log.e("StravaLoginScreen", "Failed to get athlete.")
            }

        }
    }
}
