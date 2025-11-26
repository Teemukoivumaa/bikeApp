package com.example.bikeapp.ui.screens.activities

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeapp.data.local.AppDatabase
import com.example.bikeapp.data.local.SecureStorageManager
import com.example.bikeapp.data.model.ActivityLocationEntity
import com.example.bikeapp.data.model.StravaActivityEntity
import com.example.bikeapp.data.model.mockActivity
import com.example.bikeapp.data.remote.StravaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

            if (dbActivity.fullInfoFetched || dbActivity.externalId == null) {
                _activity.value = dbActivity
                return@launch
            }

            val fullActivity = stravaRepository.getActivity(authToken,
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
}
