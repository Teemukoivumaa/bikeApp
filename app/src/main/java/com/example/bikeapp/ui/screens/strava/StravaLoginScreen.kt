package com.example.bikeapp.ui.screens.strava

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp


/**
 * This composable function launches Strava authorization using the launchStravaAuthorization function.
 */
@Composable
fun StravaLoginScreen(
    stravaLoginViewModel: StravaLoginViewModel,
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var isTokenExchanged by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!isLoading) {
            Button(
                onClick = {
                    if (stravaLoginViewModel.getAccessToken() == null) {
                        stravaLoginViewModel.launchStravaAuthorization(context)
                    } else {
                        isLoading = true
                        stravaLoginViewModel.exchangeToken(
                            coroutineScope = coroutineScope
                        )
                        isLoading = false
                        isTokenExchanged = true
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                if (stravaLoginViewModel.getAccessToken() == null) {
                    Text("Connect to Strava")
                } else {
                    Text("Exchange the code")
                }
            }
            Button(
                onClick = {
                    isLoading = true
                    stravaLoginViewModel.refreshStravaToken(
                        coroutineScope = coroutineScope
                                )
                    isLoading = false
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Refresh token")
            }
            Button(
                onClick = {
                    isLoading = true
                    stravaLoginViewModel.fetchActivities()
                    isLoading = false
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Fetch activities")
            }

            Button(
                onClick = {
                    isLoading = true
                    stravaLoginViewModel.fetchAthlete()
                    isLoading = false
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Fetch athlete info")
            }

        }
        if (isLoading) {
            CircularProgressIndicator()
        }
        if (isTokenExchanged) {
            Text(text = "You have exchanged the token")
        }
    }
//    LaunchedEffect(key1 = context) {
//        if (secureStorageManager.getAccessToken() != null) {
//            isLoading = true
////            exchangeToken(
////                secureStorageManager = secureStorageManager,
////                stravaRepository = stravaRepository,
////                coroutineScope = coroutineScope
////            )
//            isLoading = false
//        }
//    }
}