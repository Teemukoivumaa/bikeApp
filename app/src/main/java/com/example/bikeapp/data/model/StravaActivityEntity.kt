package com.example.bikeapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.bikeapp.data.remote.StreamType
import java.util.Date

@Entity(tableName = "strava_activities")
data class StravaActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val description: String?,
    val type: String,
    val distance: Float,
    val calories: Float?,
    @ColumnInfo(name = "sport_type") val sportType: String,
    @ColumnInfo(name = "moving_time") val movingTime: Int,
    @ColumnInfo(name = "elapsed_time") val elapsedTime: Int,
    @ColumnInfo(name = "start_date") val startDate: Date,
    @ColumnInfo(name = "activity_end_time") val activityEndTime: String,
    @ColumnInfo(name = "average_speed") val averageSpeed: Float,
    @ColumnInfo(name = "max_speed") val maxSpeed: Float,
    @ColumnInfo(name = "total_elevation_gain") val totalElevationGain: Float,
    @ColumnInfo(name = "elev_high") val elevHigh: Float,
    @ColumnInfo(name = "elev_low") val elevLow: Float,
    @ColumnInfo(name = "average_watts") val averageWatts: Float?,
    @ColumnInfo(name = "average_heartrate") val averageHeartrate: Float?,
    @ColumnInfo(name = "max_heartrate") val maxHeartrate: Float?,
    @ColumnInfo(name = "device_name") val deviceName: String?,
    @ColumnInfo(name = "summary_polyline") val summaryPolyline: String?,
    @ColumnInfo(name = "external_id") val externalId: String?,
    @ColumnInfo(name = "full_info_fetched") val fullInfoFetched: Boolean = false
)

@Entity(
    tableName = "activity_streams",
    foreignKeys = [ForeignKey(
        entity = StravaActivityEntity::class,
        parentColumns = ["id"],
        childColumns = ["activity_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["activity_id"])]
)
data class ActivityStreamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "activity_id") val activityId: Long,
    val type: StreamType,
    val data: List<Float>,
    @ColumnInfo(name = "series_type") val seriesType: String,
    @ColumnInfo(name = "original_size") val originalSize: Int,
    val resolution: String
)

fun mockActivity(): StravaActivityEntity {
    return StravaActivityEntity(
        id = 1,
        name = "Sample Activity",
        type = "Ride",
        distance = 28099F,
        movingTime = 60,
        elapsedTime = 60,
        startDate = Date(),
        activityEndTime = "12:00",
        averageSpeed = 10.0f,
        averageHeartrate = 10.0f,
        maxHeartrate = 20.0f,
        maxSpeed = 10.0f,
        totalElevationGain = 10.0f,
        averageWatts = 10.0f,
        externalId = "externalId",
        description = "desc",
        calories = 200F,
        sportType = "sport",
        elevHigh = 100F,
        elevLow = 10F,
        summaryPolyline = null,
        deviceName = "Mock",
    )
}

@Entity(
    tableName = "activity_locations",
    foreignKeys = [ForeignKey(
        entity = StravaActivityEntity::class,
        parentColumns = ["id"],
        childColumns = ["activity_id"],
        onDelete = ForeignKey.CASCADE // Delete the location if the activity is deleted
    )],
    indices = [Index(value = ["activity_id"])]  // Added index here
)
data class ActivityLocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "activity_id") val activityId: Long,
    val latitude: Double,
    val longitude: Double,
    val coordinatesAsString: String,
    val type: String,
)
