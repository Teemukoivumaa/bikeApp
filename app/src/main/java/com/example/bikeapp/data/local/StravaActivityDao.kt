package com.example.bikeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bikeapp.data.model.StravaActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StravaActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(activities: List<StravaActivityEntity>)

    @Query("SELECT * FROM strava_activities WHERE id = :id")
    fun getActivityById(id: Long): Flow<StravaActivityEntity>

    @Query("SELECT * FROM strava_activities")
    fun getAllActivities(): Flow<List<StravaActivityEntity>>

    @Query("DELETE FROM strava_activities")
    suspend fun deleteAllActivities()
}