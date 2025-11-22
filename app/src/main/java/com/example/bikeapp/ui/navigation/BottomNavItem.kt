package com.example.bikeapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.bikeapp.R

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector,
    val drawableId: Int? = null
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
        name = "Challenges",
        route = "challenges",
        icon = Icons.Filled.Menu, // Placeholder icon
        drawableId = R.drawable.challenges
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