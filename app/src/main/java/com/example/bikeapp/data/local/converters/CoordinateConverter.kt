package com.example.bikeapp.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CoordinateConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromList(coordinates: List<Double>): String {
        return gson.toJson(coordinates)
    }

    @TypeConverter
    fun toList(coordinatesAsString: String): List<Double> {
        val listType = object : TypeToken<List<Double>>() {}.type
        return gson.fromJson(coordinatesAsString, listType)
    }
}