package com.example.bikeapp

import android.os.Bundle
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
import com.example.bikeapp.ui.navigation.AppNavGraph
import com.example.bikeapp.ui.screens.activities.ActivityViewModel
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
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph(viewModel = viewModel)
                }
            }
        }
    }
}