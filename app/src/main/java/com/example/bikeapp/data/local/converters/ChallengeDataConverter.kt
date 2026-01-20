package com.example.bikeapp.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson

class ChallengeDataConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromChallengeData(data: List<Float>): String {
        return gson.toJson(data)
    }

    @TypeConverter
    fun toChallengeData(dataString: String): List<Float> {
        return gson.fromJson(dataString, Array<Float>::class.java).toList()
    }
}