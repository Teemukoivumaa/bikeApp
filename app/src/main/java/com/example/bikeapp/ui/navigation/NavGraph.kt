package com.example.bikeapp.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bikeapp.ui.screens.activities.ActivitiesScreen
import com.example.bikeapp.ui.screens.activities.ActivitiesViewModel
import com.example.bikeapp.ui.screens.activities.ActivityDetailsScreen
import com.example.bikeapp.ui.screens.activities.ActivityViewModel
import com.example.bikeapp.ui.screens.challenges.ChallengeCreationScreen
import com.example.bikeapp.ui.screens.challenges.ChallengeDetailsScreen
import com.example.bikeapp.ui.screens.challenges.ChallengesScreen
import com.example.bikeapp.ui.screens.home.HomeScreen
import com.example.bikeapp.ui.screens.profile.CreateAccountScreen
import com.example.bikeapp.ui.screens.profile.ProfileScreen
import com.example.bikeapp.ui.screens.strava.StravaLoginScreen
import com.example.bikeapp.ui.screens.strava.StravaLoginViewModel


@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    activityViewModel: ActivityViewModel,
    activitiesViewModel: ActivitiesViewModel,
    stravaLoginViewModel: StravaLoginViewModel,
) {
    val layoutDirection = LocalLayoutDirection.current

    // Leaves a thin line at the bottom, makes it so that the content doesn't overlap with the bottom navigation bar
    val minimumBottomPadding = 8.dp
    val reductionAmount = 48.dp

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    end = paddingValues.calculateEndPadding(layoutDirection),
                    bottom = max(minimumBottomPadding, paddingValues.calculateBottomPadding() - reductionAmount)
                )
        ) {
            NavHost(navController = navController, startDestination = "home_screen") {
                composable("home_screen") {
                    HomeScreen()
                }
                composable("activities_screen") {
                    ActivitiesScreen(
                        activityViewModel,
                        navController = navController
                    )
                }
                composable("activityDetails/{activityId}") { backStackEntry ->
                    val activityId = backStackEntry.arguments?.getString("activityId")

                    if (activityId != null) {
                        ActivityDetailsScreen(activitiesViewModel, activityId.toLong())
                    } else {
                        HomeScreen()
                    }
                }
                composable("challenges") {
                    ChallengesScreen(navController)
                }
                composable("create_challenge") {
                    ChallengeCreationScreen(navController)
                }
                composable("challengeDetails/{challengeId}") { backStackEntry ->
                    val challengeId = backStackEntry.arguments?.getString("challengeId")

                    if (challengeId != null) {
                        ChallengeDetailsScreen(navController = navController, challengeId = challengeId.toInt())
                    }
                }
                composable("strava_login") {
                    StravaLoginScreen(stravaLoginViewModel)
                }
                composable("profile_screen") {
                    ProfileScreen(navController)
                }
                composable("create_profile") {
                    CreateAccountScreen(navController)
                }
            }
        }
    }
}
