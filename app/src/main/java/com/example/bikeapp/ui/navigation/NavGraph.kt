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
import com.example.bikeapp.data.local.AppDatabase
import com.example.bikeapp.ui.screens.activities.ActivitiesScreen
import com.example.bikeapp.ui.screens.activities.ActivityDetailsPage
import com.example.bikeapp.ui.screens.activities.ActivityViewModel
import com.example.bikeapp.ui.screens.home.HomeScreen
import com.example.bikeapp.ui.screens.home.HomeScreenViewModel
import com.example.bikeapp.ui.screens.strava.StravaLoginScreen
import com.example.bikeapp.ui.screens.strava.StravaLoginViewModel


@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    database: AppDatabase,
    activityViewModel: ActivityViewModel,
    stravaLoginViewModel: StravaLoginViewModel,
    homeScreenViewModel: HomeScreenViewModel
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            NavHost(navController = navController, startDestination = "home_screen") {
                composable("home_screen") { HomeScreen(homeScreenViewModel) }
                composable("activities_screen") {
                    ActivitiesScreen(
                        activityViewModel,
                        navController = navController
                    )
                }
                composable("activityDetails/{activityId}") { backStackEntry ->
                    val activityId = backStackEntry.arguments?.getString("activityId")

                    val activityIdLong = activityId?.toLongOrNull()

                    if (activityIdLong != null) {
                        val activity =
                            database.stravaActivityDao().getActivityById(activityIdLong)
                        val locations = database.locationDao().getLocationsByActivityId(activityIdLong)
                        ActivityDetailsPage(activity = activity, locations = locations)
                    }


                }
                composable("strava_login") {
                    StravaLoginScreen(stravaLoginViewModel)
                }
            }
        }
    }
}