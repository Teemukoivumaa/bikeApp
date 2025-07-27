package com.example.bikeapp.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

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
 * Extracts the GMT offset (e.g., "+02:00", "-05:00", "Z") from a timezone string.
 *
 * @param timezoneString The string potentially containing an offset, like "(GMT+02:00) Europe/Helsinki".
 * @return The extracted offset string (e.g., "+02:00") or null if not found or invalid.
 */
fun extractGmtOffsetString(timezoneString: String): String? {
    // Regex to find patterns like +HH:MM, -HH:MM, or Z (for UTC/GMT)
    // It looks for a sign (+ or -), two digits, a colon, and two more digits.
    // Or a Z for UTC.
    val pattern = Pattern.compile("([+-][0-9]{2}:[0-9]{2}|Z)")
    val matcher = pattern.matcher(timezoneString)
    return if (matcher.find()) {
        matcher.group(0)
    } else {
        null // Or you could try to infer "Z" if "GMT" is present but no specific offset digits
    }
}

/**
 * Converts an ISO 8601 date/time string from UTC to a specified timezone and returns a Date object.
 *
 *
 * @param dateString The ISO 8601 date/time string (e.g., "2025-07-23T16:22:58Z").
 * @param timezone The timezone string (e.g., "(GMT+02:00) Europe/Helsinki").
 * @return A java.util.Date object representing the moment in time.
 */
fun convertStringToDateUsingTime(dateString: String, timezone: String): Date {
    val instant = Instant.parse(dateString)

    // Get the GMT offset from the timezone string
    val gmtOffset = extractGmtOffsetString(timezone)
    val zoneId = ZoneId.of(gmtOffset)

    // Create a ZonedDateTime. This step correctly applies the timezone offset.
    val zonedDateTime = instant.atZone(zoneId)
    val zonedInstant = zonedDateTime.toInstant()

    // Convert the Instant to a Date object.
    return Date.from(zonedInstant)
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