package com.example.bikeapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "athletes")
data class AthleteEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "user_name") val username: String?,
    @ColumnInfo(name = "first_name") val firstname: String?,
    @ColumnInfo(name = "last_name") val lastname: String?,
    @ColumnInfo(name = "city") val city: String?,
    @ColumnInfo(name = "state") val state: String?,
    @ColumnInfo(name = "country") val country: String?,
    @ColumnInfo(name = "sex") val sex: String?,
//    @ColumnInfo(name = "bikes") val bikes: List<Bike>?
)

fun mockAthlete(): AthleteEntity {
    return AthleteEntity(
        id = 0,
        username = "mock",
        firstname = "mock",
        lastname = "mock",
        city = "mock",
        state = "mock",
        country = "mock",
        sex = "mock",
    )
}
