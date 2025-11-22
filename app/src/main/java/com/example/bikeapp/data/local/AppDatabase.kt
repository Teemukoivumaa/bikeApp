package com.example.bikeapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bikeapp.data.local.converters.ChallengeTypeConverter
import com.example.bikeapp.data.local.converters.CoordinateConverter
import com.example.bikeapp.data.local.converters.DateConverter
import com.example.bikeapp.data.local.databaseAccess.AthleteDao
import com.example.bikeapp.data.local.databaseAccess.ChallengesDao
import com.example.bikeapp.data.local.databaseAccess.LocationDao
import com.example.bikeapp.data.local.databaseAccess.StravaActivityDao
import com.example.bikeapp.data.local.migrations.MIGRATION_1_2
import com.example.bikeapp.data.local.migrations.MIGRATION_2_3
import com.example.bikeapp.data.local.migrations.MIGRATION_3_4
import com.example.bikeapp.data.local.migrations.MIGRATION_4_5
import com.example.bikeapp.data.model.ActivityLocationEntity
import com.example.bikeapp.data.model.AthleteEntity
import com.example.bikeapp.data.model.ChallengeEntity
import com.example.bikeapp.data.model.StravaActivityEntity

const val DATABASE_NAME = "bike-app-database"

@Database(
    entities = [
        StravaActivityEntity::class,
        AthleteEntity::class,
        ActivityLocationEntity::class,
        ChallengeEntity::class
    ],
    version = 5,
    exportSchema = false
)

@TypeConverters(DateConverter::class, CoordinateConverter::class, ChallengeTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stravaActivityDao(): StravaActivityDao
    abstract fun athleteDao(): AthleteDao
    abstract fun locationDao(): LocationDao
    abstract fun challengeDao(): ChallengesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}