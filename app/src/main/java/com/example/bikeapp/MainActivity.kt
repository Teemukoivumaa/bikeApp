package com.example.bikeapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.bikeapp.data.local.AppDatabase
import com.example.bikeapp.data.local.AuthenticationStateKeys.STRAVA_AUTH_FINISHED
import com.example.bikeapp.data.local.SecureStorageManager
import com.example.bikeapp.data.remote.StravaRepository
import com.example.bikeapp.ui.navigation.AppNavGraph
import com.example.bikeapp.ui.screens.activities.ActivitiesViewModel
import com.example.bikeapp.ui.screens.activities.ActivityViewModel
import com.example.bikeapp.ui.screens.strava.StravaLoginViewModel
import com.example.bikeapp.ui.theme.BikeAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var activityViewModel: ActivityViewModel
    private lateinit var activitiesViewModel: ActivitiesViewModel
    private lateinit var stravaLoginViewModel: StravaLoginViewModel

    private lateinit var paddingValues: PaddingValues
    private lateinit var secureStorageManager: SecureStorageManager
    private lateinit var stravaRepository: StravaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getInstance(applicationContext)

        secureStorageManager = SecureStorageManager(applicationContext)

        stravaRepository = StravaRepository()

        enableEdgeToEdge()
        paddingValues = PaddingValues()

        activityViewModel = ActivityViewModel(database)
        stravaLoginViewModel =
            StravaLoginViewModel(secureStorageManager, stravaRepository)
        activitiesViewModel = ActivitiesViewModel(stravaRepository, secureStorageManager, database)

        setContent {
            BikeAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph()
                }
            }
        }
    }

    /**
     * This function is called when the user navigates back from the Strava authorization flow.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleAuthorizationResponse(intent)
    }

    /**
     * This function handles the response from the Strava authorization flow.
     *
     * The intent uri should contain the authorization code and the scope.
     */
    private fun handleAuthorizationResponse(intent: Intent?) {
        val uri: Uri? = intent?.data
        Log.d("MainActivity", "URI: $uri")
        if (uri != null) {
            if (uri.toString().startsWith("http://localhost")) {
                val code = uri.getQueryParameter("code")
                val scope = uri.getQueryParameter("scope")

                if (code != null) {
                    Log.d("MainActivity", "Authorization Code: $code")

                    secureStorageManager.saveAccessToken(code)
                    secureStorageManager.saveAuthenticationState(STRAVA_AUTH_FINISHED)
                } else {
                    Log.e("MainActivity", "Authorization code not found.")
                }

                if (scope != null) {
                    Log.d("MainActivity", "Authorization Scope: $scope")

                    secureStorageManager.saveScope(scope)
                } else {
                    Log.e("MainActivity", "Authorization scope not found.")
                }
            } else if (uri.toString().startsWith("bikeapp://callback")) {
                val code = uri.getQueryParameter("code")
                val scope = uri.getQueryParameter("scope")

                if (code != null) {
                    Log.d("MainActivity", "Authorization Code: $code")
                    secureStorageManager.saveAccessToken(code)
                    secureStorageManager.saveAuthenticationState(STRAVA_AUTH_FINISHED)
                } else {
                    Log.e("MainActivity", "Authorization code not found.")
                }

                if (scope != null) {
                    Log.d("MainActivity", "Authorization Scope: $scope")

                    secureStorageManager.saveScope(scope)
                } else {
                    Log.e("MainActivity", "Authorization scope not found.")
                }
            }
        }
    }
}