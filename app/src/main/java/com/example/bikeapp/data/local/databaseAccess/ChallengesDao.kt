package com.example.bikeapp.data.local.databaseAccess

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bikeapp.data.model.ChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(challenge: ChallengeEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(challenges: List<ChallengeEntity>)

    @Query("SELECT * FROM challenges")
    fun getAllChallenges(): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE id = :id")
    suspend fun getChallengeById(id: Int): ChallengeEntity?

    @Query("SELECT * FROM challenges WHERE name = :name")
    fun getChallengeByName(name: String): ChallengeEntity?

    @Query("SELECT * FROM challenges WHERE is_active = 1")
    fun getActiveChallenges(): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE is_completed = 1")
    fun getCompletedChallenges(): List<ChallengeEntity>

    @Query("DELETE FROM challenges WHERE id = :id")
    suspend fun deleteChallengeById(id: Int)

    @Update
    suspend fun update(challenge: ChallengeEntity)
}