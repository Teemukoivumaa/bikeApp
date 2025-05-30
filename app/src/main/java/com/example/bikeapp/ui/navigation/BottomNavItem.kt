package com.example.bikeapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        name = "Home",
        route = "home_screen",
        icon = Icons.Filled.Home
    ),
    BottomNavItem(
        name = "Activities",
        route = "activities_screen",
        icon = Icons.AutoMirrored.Filled.List
    ),
    BottomNavItem(
        name = "Strava",
        route = "strava_login",
        icon = Icons.Filled.Settings
    ),
    BottomNavItem(
        name = "Profile",
        route = "profile_screen",
        icon = Icons.Filled.Person
    )
)