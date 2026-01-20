package com.example.bikeapp.data.local.databaseAccess

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bikeapp.data.model.ActivityStreamEntity

@Dao
interface ActivityStreamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stream: ActivityStreamEntity)

    @Query("SELECT * FROM activity_streams WHERE activity_id = :activityId")
    suspend fun getStreamsByActivityId(activityId: Long): List<ActivityStreamEntity>
}
