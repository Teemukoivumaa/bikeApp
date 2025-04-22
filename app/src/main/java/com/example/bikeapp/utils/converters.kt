package com.example.bikeapp.utils

import java.util.Locale

/**
 * Converts a speed in meters per second (m/s) to kilometers per hour (km/h).
 *
 * @param speedMs The speed in meters per second.
 * @return The speed in kilometers per hour.
 */
fun convertMsToKmh(speedMs: Float): String {
    val speedInKmh = speedMs * 3.6
    // Modify the format to include only two decimal places
    return String.format(Locale.getDefault(), "%.2f", speedInKmh)
}

/**
 * Converts a distance in meters to kilometers.
 *
 * If the distance is less than 1000 meters, it returns the distance in meters.
 * Otherwise, it converts the distance to kilometers and returns it.
 * @param distance The distance in meters.
 * @return The distance in kilometers.
 */
fun convertMtoKm(distance: Float): String {
    // If distance is less than 1000 meters, return the distance in meters
    if (distance < 1000) {
        return String.format(Locale.getDefault(), "%.2f m", distance)
    }

    val distanceInKm = distance / 1000

    return String.format(Locale.getDefault(), "%.2f km", distanceInKm)
}