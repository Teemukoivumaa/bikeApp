package com.example.bikeapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BikeApplication : Application() {
    // You can leave this class empty for now, or add
    // application-level initialization code here in the future
    // (e.g., setting up logging, analytics, etc.)
    override fun onCreate() {
        super.onCreate()
    }
}