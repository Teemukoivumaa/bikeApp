package com.example.bikeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bikeapp.data.model.AthleteEntity

@Dao
interface AthleteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAthlete(athlete: AthleteEntity)

    @Query("SELECT * FROM athletes WHERE id = :id")
    suspend fun getAthleteById(id: Long): AthleteEntity?

    @Query("SELECT * FROM athletes LIMIT 1")
    suspend fun getAthlete(): AthleteEntity?
}