package com.example.bikeapp.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

const val DATE_FORMAT = "dd.MM.yyyy"
const val DATE_WITH_TIME_FORMAT = "dd.MM.yyyy, HH:mm"
const val HOURS_MINUTES_FORMAT = "HH:mm"

/**
 * Formats a Date object to a user-friendly string using the specified format.
 *
 * @param date The Date object to format.
 * @param format The format string to use for formatting the date.
 * @return A formatted string representing the date.
 */
fun formatDate(date: Date, format: String = DATE_WITH_TIME_FORMAT): String {
    val format = SimpleDateFormat(format, Locale.getDefault())
    return format.format(date)
}

/**
 * Converts an ISO 8601 date/time string to a Date using the modern java.time API.
 *
 * @param dateString The ISO 8601 date/time string (e.g., "2025-01-06T14:22:08Z").
 * @return A java.util.Date object representing the date/time.
 */
fun convertStringToDateUsingTime(dateString: String): Date {
    val formatter = DateTimeFormatter.ISO_INSTANT
    val instant = Instant.from(formatter.parse(dateString))
    return Date.from(instant)
}

/**
 * Formats a duration in seconds to a user-friendly string (e.g., "1 hour 30 minutes").
 * @param durationSeconds The duration in seconds.
 * @return A formatted string representing the duration.
 */
fun formatDuration(durationSeconds: Int): String {
    val hours = durationSeconds / 3600
    val minutes = (durationSeconds % 3600) / 60
    return buildString {
        if (hours > 0) {
            append("$hours hour${if (hours > 1) "s" else ""} ")
        }
        if (minutes > 0 || hours == 0) {
            append("$minutes minute${if (minutes != 1) "s" else ""}")
        }
    }
}

/**
 * Calculates the end time based on a start time and a duration in seconds.
 * @param startTime The start time as a Date object.
 * @param durationSeconds The duration in seconds.
 * @return A formatted string representing the end time.
 */
fun calculateEndTime(startTime: Date, durationSeconds: Int): String {
    val calendar = java.util.Calendar.getInstance()
    calendar.time = startTime
    calendar.add(java.util.Calendar.SECOND, durationSeconds)

    var startDate = formatDate(startTime, DATE_FORMAT)
    val endDate = formatDate(calendar.time, DATE_FORMAT)

    // If endTime has the same day as startTime, return only the HH:mm format
    if (startDate == endDate) {
        return formatDate(calendar.time, HOURS_MINUTES_FORMAT)
    }

    // Otherwise, return dd.MM.yyyy, HH:mm
    return formatDate(calendar.time, DATE_WITH_TIME_FORMAT)
}