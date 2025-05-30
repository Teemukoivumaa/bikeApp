package com.example.bikeapp.ui.screens.strava

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bikeapp.R

/**
 * This composable function launches Strava authorization using the launchStravaAuthorization function.
 */
@Composable
fun StravaLoginScreen(
    stravaLoginViewModel: StravaLoginViewModel,
) {
    val context = LocalContext.current
    val sharedAuthViewModel: SharedAuthViewModel = hiltViewModel()

    stravaLoginViewModel.checkIfShouldContinueAuth()

    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        }
        if (!isLoading) {
            IconButton(
                onClick = {
                    if (!sharedAuthViewModel.isAuthenticated()) {
                        stravaLoginViewModel.launchStravaAuthorization(context)
                    } else {
                        // Or a different action if this button is for something else
                        Toast.makeText(context, "Already authenticated!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(fraction = 0.8f)
                    .requiredHeight(40.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.strava_connect),
                    contentDescription = "Connect to Strava Icon",
                )
            }
        }
    }
}