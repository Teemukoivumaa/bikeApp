package com.example.bikeapp.ui.screens.strava

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeapp.BuildConfig
import com.example.bikeapp.data.local.AppDatabase
import com.example.bikeapp.data.local.AuthenticationStateKeys.AUTHENTICATED
import com.example.bikeapp.data.local.AuthenticationStateKeys.STRAVA_AUTH_FINISHED
import com.example.bikeapp.data.local.AuthenticationStateKeys.STRAVA_AUTH_STARTED
import com.example.bikeapp.data.local.AuthenticationStateKeys.UNAUTHENTICATED
import com.example.bikeapp.data.local.SecureStorageManager
import com.example.bikeapp.data.remote.StravaRepository
import com.example.bikeapp.data.remote.TokenRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class StravaLoginViewModel(
    private val secureStorageManager: SecureStorageManager,
    private val stravaRepository: StravaRepository
) : ViewModel() {

    fun checkIfShouldContinueAuth() {
        val shouldContinue = checkIfShouldExchangeToken()

        if (shouldContinue) {
            exchangeToken(viewModelScope)
        }
        return
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

        val intent = Intent(Intent.ACTION_VIEW, intentUri)

        // If user has Strava app installed, will open Strava app, otherwise browser
        if (intent.resolveActivity(context.packageManager) != null) {
            secureStorageManager.saveAuthenticationState(STRAVA_AUTH_STARTED)
            context.startActivity(intent)
        } else {
            Log.e("StravaLogin", "No activity found to handle Strava authorization intent.")
            // You might want to display an error message to the user here.
        }
    }

    /**
     * This function checks if the user should exchange the authorization code for an access token.
     * This is done after user has approved the API access through Strava.
     */
    fun checkIfShouldExchangeToken(): Boolean {
        val state = secureStorageManager.getAuthenticationState()

        return state == STRAVA_AUTH_FINISHED
    }

    /**
     * This function exchanges the authorization code for an access token so that user is fully authenticated.
     *
     * @param coroutineScope The coroutine scope to launch the coroutine.
     */
    fun exchangeToken(
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            Log.i("StravaLoginScreen", "Trying to exchange token.")
            val code = secureStorageManager.getAccessToken()

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

                    secureStorageManager.saveAuthenticationState(AUTHENTICATED)
                    Log.i("StravaLoginScreen", "Successfully authenticated.")
                } else {
                    // Token exchange failed, need to re auth
                    secureStorageManager.deleteAccessToken()
                    secureStorageManager.deleteRefreshToken()
                    secureStorageManager.deleteExpiresAt()
                    secureStorageManager.deleteExpiresIn()
                    secureStorageManager.saveAuthenticationState(UNAUTHENTICATED)
                    Log.e("StravaLoginScreen", "Failed to exchange token.")
                }
            } else {
                Log.e("StravaLoginScreen", "Access token is null. Can't exchange token.")
            }
        }
    }
}
