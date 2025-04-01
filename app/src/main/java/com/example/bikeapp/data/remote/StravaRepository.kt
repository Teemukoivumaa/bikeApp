package com.example.bikeapp.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class StravaRepository(private val stravaApi: StravaApi = RetrofitClient.instance) {

    suspend fun exchangeToken(tokenRequest: TokenRequest): TokenResponse? =
        withContext(Dispatchers.IO) {
            try {
                val response = stravaApi.exchangeToken(tokenRequest)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("StravaRepository", "Error exchanging token: $errorBody")

                    null
                }
            } catch (e: HttpException) {
                Log.e("StravaRepository", "HttpException: ${e.message()}")
                null
            } catch (e: IOException) {
                Log.e("StravaRepository", "IOException: ${e.message}")
                null
            }
        }

    suspend fun getRefreshToken(refreshTokenRequest: RefreshTokenRequest): RefreshTokenResponse? =
        withContext(Dispatchers.IO) {
            try {
                val response = stravaApi.refreshToken(
                    refreshTokenRequest
                )
                if (response.isSuccessful) {
                    response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("StravaRepository", "Error refreshing token: $errorBody")
                    null
                }
            } catch (e: HttpException) {
                Log.e("StravaRepository", "HttpException: ${e.message()}")
                null
            } catch (e: IOException) {
                Log.e("StravaRepository", "IOException: ${e.message}")
                null
            }
        }


    suspend fun getAthleteActivities(
        authorization: String, perPage: Int?, page: Int?
    ): List<ActivityResponse>? = withContext(Dispatchers.IO) {
        val authorizationHeader = "Bearer $authorization"

        try {
            val response = stravaApi.getAthleteActivities(authorizationHeader, perPage, page)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e(
                    "StravaRepository",
                    "Error getAthleteActivities: ${response.errorBody()?.string()}"
                )
                null
            }
        } catch (e: HttpException) {
            Log.e("StravaRepository", "HttpException: ${e.message()}")
            null
        } catch (e: IOException) {
            Log.e("StravaRepository", "IOException: ${e.message}")
            null
        }
    }

    suspend fun getAthleteInfo(authorization: String): Athlete? = withContext(Dispatchers.IO) {
        val authorizationHeader = "Bearer $authorization"
        Log.d("StravaRepository", "getAthleteInfo authorization: $authorizationHeader")

        try {
            val response = stravaApi.getAthlete(authorizationHeader)
            Log.d("StravaRepository", "getAthleteInfo: $response")

            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("StravaRepository", "Error getAthleteInfo: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: HttpException) {
            Log.e("StravaRepository", "HttpException: ${e.message()}")
            null
        } catch (e: IOException) {

            Log.e("StravaRepository", "IOException: ${e.message}")
            null
        }
    }
}