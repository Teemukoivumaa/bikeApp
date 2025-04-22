package com.example.bikeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bikeapp.data.local.converters.CoordinateConverter
import com.example.bikeapp.data.local.converters.DateConverter
import com.example.bikeapp.data.model.ActivityLocationEntity
import com.example.bikeapp.data.model.AthleteEntity
import com.example.bikeapp.data.model.StravaActivityEntity

@Database(
    entities = [StravaActivityEntity::class, AthleteEntity::class, ActivityLocationEntity::class],
    version = 3,
    exportSchema = true
)

@TypeConverters(DateConverter::class, CoordinateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stravaActivityDao(): StravaActivityDao
    abstract fun stravaAthleteDao(): AthleteDao
    abstract fun locationDao(): LocationDao
}