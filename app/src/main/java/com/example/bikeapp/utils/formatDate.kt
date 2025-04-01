package com.example.bikeapp.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun formatDate(date: Date): String {
    val format = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault())
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