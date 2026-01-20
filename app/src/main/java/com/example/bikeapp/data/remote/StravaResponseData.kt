package com.example.bikeapp.data.remote

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    val token_type: String,
    val expires_at: Int,
    val expires_in: Int,
    val refresh_token: String,
    val access_token: String,
    val athlete: Athlete
)

data class RefreshTokenResponse(
    val token_type: String,
    val access_token: String,
    val expires_at: Int,
    val expires_in: Int,
    val refresh_token: String,
)

data class Bike(
    val id: String,
    val primary: Boolean,
    val name: String,
    @SerializedName("resource_state")
    val resourceState: Int,
    val distance: Int
)

data class Athlete(
    val id: Int,
    val username: String,
    val firstname: String,
    val lastname: String,
    val city: String,
    val state: String,
    val country: String,
    val sex: String,
    val bikes: List<Bike>, // Can be null or empty
)

data class TokenRequest(
    val client_id: String,
    val client_secret: String,
    val code: String,
    val grant_type: String = "authorization_code"
)

data class RefreshTokenRequest(
    val client_id: String,
    val client_secret: String,
    val refresh_token: String,
    val grant_type: String = "refresh_token"
)

data class ActivityResponse(
    @SerializedName("resource_state")
    val resourceState: Int,
    val athlete: ActivityAthlete,
    val name: String,
    val distance: Float,
    val id: Long,
    val type: String,
    val timezone: String,
    val description: String?, // Can be null
    @SerializedName("moving_time")
    val movingTime: Int,
    @SerializedName("elapsed_time")
    val elapsedTime: Int,
    @SerializedName("total_elevation_gain")
    val totalElevationGain: Float,
    @SerializedName("elev_high")
    val elevHigh: Float,
    @SerializedName("elev_low")
    val elevLow: Float,
    @SerializedName("sport_type")
    val sportType: String,
    @SerializedName("workout_type")
    val workoutType: Any?, // Can be null, so use Any? or a specific type if you know it
    @SerializedName("external_id")
    val externalId: String?, // Can be null
    @SerializedName("upload_id")
    val uploadId: Long,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("start_date_local")
    val startDateLocal: String,
    @SerializedName("utc_offset")
    val utcOffset: Int,
    @SerializedName("start_latlng")
    val startLatlng: List<Double>?, // Can be null
    @SerializedName("end_latlng")
    val endLatlng: List<Double>?, // Can be null
    @SerializedName("location_city")
    val locationCity: String?, // Can be null
    @SerializedName("location_state")
    val locationState: String?, // Can be null
    @SerializedName("location_country")
    val locationCountry: String,
    @SerializedName("achievement_count")
    val achievementCount: Int,
    @SerializedName("kudos_count")
    val kudosCount: Int,
    @SerializedName("comment_count")
    val commentCount: Int,
    @SerializedName("athlete_count")
    val athleteCount: Int,
    @SerializedName("photo_count")
    val photoCount: Int,
    val map: ActivityMap,
    val trainer: Boolean,
    val commute: Boolean,
    val manual: Boolean,
    val calories: Float?, // Can be null
    @SerializedName("private")
    val isPrivate: Boolean, // 'private' is a reserved keyword in Kotlin
    val flagged: Boolean,
    @SerializedName("gear_id")
    val gearId: String?, // Can be null
    @SerializedName("from_accepted_tag")
    val fromAcceptedTag: Boolean,
    @SerializedName("average_speed")
    val averageSpeed: Float,
    @SerializedName("max_speed")
    val maxSpeed: Float,
    @SerializedName("average_cadence")
    val averageCadence: Double?, // Can be null
    @SerializedName("average_watts")
    val averageWatts: Float?, // Can be null
    @SerializedName("weighted_average_watts")
    val weightedAverageWatts: Int?, // Can be null
    val kilojoules: Double?, // Can be null
    @SerializedName("device_watts")
    val deviceWatts: Boolean?, // Can be null
    @SerializedName("device_name")
    val deviceName: String?, // Can be null
    @SerializedName("has_heartrate")
    val hasHeartrate: Boolean,
    @SerializedName("average_heartrate")
    val averageHeartrate: Float?, // Can be null
    @SerializedName("max_heartrate")
    val maxHeartrate: Float?, // Can be null
    @SerializedName("max_watts")
    val maxWatts: Int?, // Can be null
    @SerializedName("pr_count")
    val prCount: Int,
    @SerializedName("total_photo_count")
    val totalPhotoCount: Int,
    @SerializedName("has_kudoed")
    val hasKudoed: Boolean,
    @SerializedName("suffer_score")
    val sufferScore: Int? // Can be null
)

enum class StreamType {
    @SerializedName("time")
    TIME,
    @SerializedName("altitude")
    ALTITUDE,
    @SerializedName("velocity_smooth")
    VELOCITY_SMOOTH,
    @SerializedName("heartrate")
    HEARTRATE,
    @SerializedName("cadence")
    CADENCE,
    @SerializedName("watts")
    WATTS,
    @SerializedName("temp")
    TEMP,
//    @SerializedName("moving") // This returns a boolean data value, removing for now
//    MOVING,
    @SerializedName("grade_smooth")
    GRADE_SMOOTH,
    @SerializedName("distance")
    DISTANCE
}

data class ActivityStreamsResponse(
    val streams: List<ActivityStream>
)

data class ActivityStream(
    val type: StreamType,
    val data: List<Float>,
    @SerializedName("series_type")
    val seriesType: String,
    @SerializedName("original_size")
    val originalSize: Int,
    val resolution: String
)

data class ActivityAthlete(
    val id: Int,
    @SerializedName("resource_state")
    val resourceState: Int
)

data class ActivityMap(
    val id: String,
    @SerializedName("summary_polyline")
    val summaryPolyline: String?, // Can be null
    @SerializedName("resource_state")
    val resourceState: Int
)
