package com.example.bikeapp.data.local.databaseAccess

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bikeapp.data.model.ActivityLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insert(location: ActivityLocationEntity): Long

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertAll(locations: List<ActivityLocationEntity>)

    @Query("SELECT * FROM activity_locations WHERE id = :id")
    fun getLocationsById(id: Long): Flow<List<ActivityLocationEntity>>

    // Select all locations for a specific activity, ordered by type in desc order so "Start" is first
    @Query("SELECT * FROM activity_locations WHERE activity_id = :id ORDER BY type DESC")
    fun getLocationsByActivityId(id: Long): List<ActivityLocationEntity>
}