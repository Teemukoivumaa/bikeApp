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
import androidx.room.Room
import com.example.bikeapp.data.local.AppDatabase
import com.example.bikeapp.data.local.SecureStorageManager
import com.example.bikeapp.data.local.migrations.MIGRATION_1_2
import com.example.bikeapp.data.local.migrations.MIGRATION_2_3
import com.example.bikeapp.data.remote.StravaRepository
import com.example.bikeapp.ui.navigation.AppNavGraph
import com.example.bikeapp.ui.screens.activities.ActivityViewModel
import com.example.bikeapp.ui.screens.home.HomeScreenViewModel
import com.example.bikeapp.ui.screens.strava.StravaLoginViewModel
import com.example.bikeapp.ui.theme.BikeAppTheme

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var activityViewModel: ActivityViewModel
    private lateinit var stravaLoginViewModel: StravaLoginViewModel
    private lateinit var homeScreenViewModel: HomeScreenViewModel

    private lateinit var paddingValues: PaddingValues
    private lateinit var secureStorageManager: SecureStorageManager
    private lateinit var stravaRepository: StravaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "bike-app-database"
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()

        secureStorageManager = SecureStorageManager(applicationContext)

        stravaRepository = StravaRepository()

        enableEdgeToEdge()
        paddingValues = PaddingValues()

        activityViewModel = ActivityViewModel(database)
        stravaLoginViewModel =
            StravaLoginViewModel(database, secureStorageManager, stravaRepository)
        homeScreenViewModel = HomeScreenViewModel(database)

        setContent {
            BikeAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph(
                        database = database,
                        activityViewModel = activityViewModel,
                        stravaLoginViewModel = stravaLoginViewModel,
                        homeScreenViewModel = homeScreenViewModel
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("MainActivity", "onNewIntent: $intent")
        handleAuthorizationResponse(intent)
    }

    private fun handleAuthorizationResponse(intent: Intent?) {
        val uri: Uri? = intent?.data
        Log.d("MainActivity", "URI: $uri")
        if (uri != null) {
            if (uri.toString().startsWith("http://localhost")) {
                val code = uri.getQueryParameter("code")
                if (code != null) {
                    Log.d("MainActivity", "Authorization Code: $code")

                    secureStorageManager.saveAccessToken(code)
                } else {
                    Log.e("MainActivity", "Authorization code not found.")
                }
            } else if (uri.toString().startsWith("bikeapp://callback")) {
                val code = uri.getQueryParameter("code")
                if (code != null) {
                    Log.d("MainActivity", "Authorization Code: $code")
                    secureStorageManager.saveAccessToken(code)
                } else {
                    Log.e("MainActivity", "Authorization code not found.")
                }
            }
        }
    }
}