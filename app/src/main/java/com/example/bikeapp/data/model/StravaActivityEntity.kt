package com.example.bikeapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "strava_activities")
data class StravaActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val type: String,
    val distance: Float,
    @ColumnInfo(name = "moving_time") val movingTime: Int,
    @ColumnInfo(name = "elapsed_time") val elapsedTime: Int,
    @ColumnInfo(name = "start_date") val startDate: Date,
    @ColumnInfo(name = "average_speed") val averageSpeed: Float,
    @ColumnInfo(name = "max_speed") val maxSpeed: Float,
    @ColumnInfo(name = "total_elevation_gain") val totalElevationGain: Float,
    @ColumnInfo(name = "average_watts") val averageWatts: Float?,
    @ColumnInfo(name = "external_id") val externalId: String?
)