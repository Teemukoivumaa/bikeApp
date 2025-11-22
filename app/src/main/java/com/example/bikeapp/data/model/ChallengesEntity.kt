package com.example.bikeapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

val recurringChallengeOptions = listOf("Weekly", "Monthly", "Yearly")

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "goal") val goal: Float,
    @ColumnInfo(name = "current_progress") val currentProgress: Float,
    @ColumnInfo(name = "unit") val unit: ChallengeUnit,
    @ColumnInfo(name = "start_date") val startDate: Date,
    @ColumnInfo(name = "end_date") val endDate: Date,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false,
    @ColumnInfo(name = "recurring") val recurring: String? = null,
)

fun mockChallenges(): List<ChallengeEntity> {
    return listOf(
        ChallengeEntity(
            id = 1,
            name = "Monthly Mileage Master",
            description = "Cycle 200 kilometers this month to earn your badge!",
            goal = 200f,
            currentProgress = 120f,
            unit = ChallengeUnit.KM,
            startDate = Date(),
            endDate = Date(),
            isActive = true
        ),
    )
}
