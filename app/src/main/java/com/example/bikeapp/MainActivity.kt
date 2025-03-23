package com.example.bikeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.bikeapp.data.local.AppDatabase
import com.example.bikeapp.ui.screens.ActivityScreen
import com.example.bikeapp.ui.screens.ActivityViewModel
import com.example.bikeapp.ui.theme.BikeAppTheme

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var viewModel: ActivityViewModel
    private lateinit var paddingValues: PaddingValues

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "bike-app-database"
        ).build()
        enableEdgeToEdge()
        viewModel = ActivityViewModel(database)
        paddingValues = PaddingValues()

        setContent {
            BikeAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "activity_screen"){
                    composable("activity_screen"){
                        ActivityScreen(viewModel, paddingValues)
                    }
                }
            }
        }
//        setContent {
//            BikeAppTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BikeAppTheme {
        Greeting("Android")
    }
}