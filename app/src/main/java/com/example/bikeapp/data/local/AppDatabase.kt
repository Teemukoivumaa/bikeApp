package com.example.bikeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bikeapp.data.model.AthleteEntity
import com.example.bikeapp.data.model.StravaActivityEntity

@Database(
    entities = [StravaActivityEntity::class, AthleteEntity::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stravaActivityDao(): StravaActivityDao
    abstract fun athleteDao(): AthleteDao
}