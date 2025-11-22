package com.example.bikeapp.data.local.converters

import androidx.room.TypeConverter
import com.example.bikeapp.data.model.ChallengeUnit

class ChallengeTypeConverter {
    @TypeConverter
    fun fromChallengeUnit(unit: ChallengeUnit): String = unit.unit

    @TypeConverter
    fun toChallengeUnit(unitString: String): ChallengeUnit {
        return ChallengeUnit.entries.first { it.unit.equals(unitString, ignoreCase = true) }
    }
}
