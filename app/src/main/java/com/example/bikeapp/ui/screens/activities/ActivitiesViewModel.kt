package com.example.bikeapp.ui.screens.activities

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeapp.data.local.AppDatabase
import com.example.bikeapp.data.local.SecureStorageManager
import com.example.bikeapp.data.model.ActivityLocationEntity
import com.example.bikeapp.data.model.ActivityStreamEntity
import com.example.bikeapp.data.model.StravaActivityEntity
import com.example.bikeapp.data.model.mockActivity
import com.example.bikeapp.data.remote.StravaRepository
import com.example.bikeapp.data.remote.StreamType
import com.example.bikeapp.utils.calculateMsToKmh
import com.example.bikeapp.utils.formatDurationHHMMSS
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

@HiltViewModel
class ActivitiesViewModel @Inject constructor(
    private val stravaRepository: StravaRepository,
    private val secureStorageManager: SecureStorageManager,
    private val database: AppDatabase
) : ViewModel() {
    private val _activity = MutableStateFlow(mockActivity())
    val activity = _activity as StateFlow<StravaActivityEntity?>

    private val _locations = MutableStateFlow<List<ActivityLocationEntity>>(emptyList())
    val locations = _locations as StateFlow<List<ActivityLocationEntity>>

    private val _streams = MutableStateFlow<List<ActivityStreamEntity>>(emptyList())
    val streams = _streams as StateFlow<List<ActivityStreamEntity>>

    private val _chartData = MutableStateFlow<List<Double>>(emptyList())
    val chartData = _chartData as StateFlow<List<Double>>

    private val _xLabels = MutableStateFlow<List<String>>(emptyList())
    val xLabels = _xLabels as StateFlow<List<String>>

    private val _yAxisLabel = MutableStateFlow("")
    val yAxisLabel = _yAxisLabel as StateFlow<String>


    fun loadActivity(id: Long) {
        val authToken = secureStorageManager.getAccessToken()
        if (authToken == null) {
            Log.e("StravaLoginScreen", "Access token is null")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val locations = withContext(Dispatchers.IO) {
                database.locationDao().getLocationsByActivityId(id)
            }
            _locations.value = locations

            val dbActivity = withContext(Dispatchers.IO) {
                database.stravaActivityDao().getActivityById(id)
            }

            val existingStreams = database.activityStreamDao().getStreamsByActivityId(dbActivity.id)
            if (existingStreams.isNotEmpty()) {
                _streams.value = existingStreams
                Log.d("ActivitiesViewModel", "Loaded streams from DB.")
            } else if (dbActivity.externalId != null) {
                val fetchedStreams = mutableListOf<ActivityStreamEntity>()
                val streamResponse = stravaRepository.getActivityStreams(
                    authToken,
                    dbActivity.externalId.toLong()
                )

                if (streamResponse != null) {
                    for (stream in streamResponse.streams) {
                        fetchedStreams.add(
                            ActivityStreamEntity(
                                activityId = dbActivity.id,
                                type = stream.type,
                                data = stream.data,
                                seriesType = stream.seriesType,
                                originalSize = stream.originalSize,
                                resolution = stream.resolution
                            )
                        )
                    }
                }
                _streams.value = fetchedStreams

                if (fetchedStreams.isNotEmpty()) {
                    fetchedStreams.forEach { streamEntity ->
                        database.activityStreamDao().insert(streamEntity)
                    }
                }
            }

            if (dbActivity.fullInfoFetched || dbActivity.externalId == null) {
                _activity.value = dbActivity
                return@launch
            }

            val fullActivity = stravaRepository.getActivity(
                authToken,
                dbActivity.externalId.toLong()
            )

            if (fullActivity != null) {
                // Update the activity with the full info
                val newActivity = dbActivity.copy(
                    description = fullActivity.description,
                    calories = fullActivity.calories,
                    deviceName = fullActivity.deviceName,
                    fullInfoFetched = true,
                ).also {
                    Log.d("ActivitiesViewModel", "Full activity fetched!")
                }

                database.stravaActivityDao().updateActivityDetails(newActivity)
                _activity.value = newActivity
            } else {
                _activity.value = dbActivity
                Log.e("ActivitiesViewModel", "Failed to fetch full activity")
            }
        }
    }
    fun processChartData(selectedStreamType: StreamType) {
        viewModelScope.launch(Dispatchers.Default) {
            val selectedStream = streams.value.find { it.type == selectedStreamType }
            if (selectedStream != null) {
                val timeStream = streams.value.find { it.type == StreamType.TIME }

                val allLabels = timeStream?.data?.map { formatDurationHHMMSS(it.toInt()) }
                    ?: (0 until selectedStream.data.size).map { it.toString() }

                _xLabels.value = if (allLabels.size <= 7) {
                    allLabels
                } else {
                    val step = (allLabels.size - 1) / 6.0
                    (0..6).map { i ->
                        val index = (i * step).roundToInt().coerceIn(allLabels.indices)
                        allLabels[index]
                    }.distinct()
                }

                _yAxisLabel.value = when (selectedStream.type) {
                    StreamType.HEARTRATE -> "Heart Rate (BPM)"
                    StreamType.ALTITUDE -> "Elevation (M)"
                    StreamType.VELOCITY_SMOOTH -> "Speed (KM/H)"
                    StreamType.CADENCE -> "Cadence (RPM)"
                    StreamType.WATTS -> "Power (W)"
                    StreamType.TEMP -> "Temperature (°C)"
                    StreamType.GRADE_SMOOTH -> "Grade (%)"
                    else -> selectedStream.type.name.lowercase().replaceFirstChar { it.titlecase() }
                }

                _chartData.value = if (selectedStream.type == StreamType.VELOCITY_SMOOTH) {
                    selectedStream.data.map { calculateMsToKmh(it) }
                } else {
                    selectedStream.data.map { it.toDouble() }
                }
            }
        }
    }
}