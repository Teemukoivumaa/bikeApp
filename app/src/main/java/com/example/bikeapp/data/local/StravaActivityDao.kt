package com.example.bikeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bikeapp.data.model.StravaActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StravaActivityDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(activities: List<StravaActivityEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: StravaActivityEntity): Long

    @Update
    fun updateActivityDetails(activity: StravaActivityEntity)

    @Query("SELECT * FROM strava_activities WHERE id = :id")
    fun getActivityById(id: Long): StravaActivityEntity

    @Query("SELECT * FROM strava_activities WHERE external_id = :externalId")
    suspend fun getActivityByExternalId(externalId: String): StravaActivityEntity?

    @Query("SELECT * FROM strava_activities")
    fun getAllActivities(): Flow<List<StravaActivityEntity>>

    @Query("SELECT * FROM strava_activities ORDER BY start_date DESC")
    fun getAllActivitiesSortedByDate(): Flow<List<StravaActivityEntity>>

    @Query("SELECT * FROM strava_activities ORDER BY start_date DESC LIMIT 1")
    fun getLatestActivity(): Flow<StravaActivityEntity>

    @Query("DELETE FROM strava_activities WHERE id = :id")
    suspend fun deleteActivityById(id: Long)

    @Query("DELETE FROM strava_activities")
    suspend fun deleteAllActivities()

    @Query("UPDATE strava_activities SET activity_end_time = :string WHERE id = :id")
    suspend fun updateActivityEndTime(id: Long, string: String)
}