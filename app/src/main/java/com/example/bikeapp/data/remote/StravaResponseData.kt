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
    @SerializedName("moving_time")
    val movingTime: Int,
    @SerializedName("elapsed_time")
    val elapsedTime: Int,
    @SerializedName("total_elevation_gain")
    val totalElevationGain: Float,
    val type: String,
    @SerializedName("sport_type")
    val sportType: String,
    @SerializedName("workout_type")
    val workoutType: Any?, // Can be null, so use Any? or a specific type if you know it
    val id: Long,
    @SerializedName("external_id")
    val externalId: String?, // Can be null
    @SerializedName("upload_id")
    val uploadId: Long,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("start_date_local")
    val startDateLocal: String,
    val timezone: String,
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
    @SerializedName("has_heartrate")
    val hasHeartrate: Boolean,
    @SerializedName("average_heartrate")
    val averageHeartrate: Double?, // Can be null
    @SerializedName("max_heartrate")
    val maxHeartrate: Int?, // Can be null
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