package com.example.bikeapp.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bikeapp.ui.screens.activities.ActivitiesScreen
import com.example.bikeapp.ui.screens.activities.ActivityViewModel
import com.example.bikeapp.ui.screens.strava.StravaLoginScreen
import com.example.bikeapp.ui.screens.strava.StravaLoginViewModel


@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    activityViewModel: ActivityViewModel,
    stravaLoginViewModel: StravaLoginViewModel,
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            NavHost(navController = navController, startDestination = "home_screen") {
                composable("home_screen") { HomeScreen() }
                composable("activities_screen") {
                    ActivitiesScreen(
                        activityViewModel,
                    )
                }
                composable("settings_screen") { SettingsScreen() }
                composable("strava_login") {
                    StravaLoginScreen(stravaLoginViewModel)
                }
            }
        }
    }
}