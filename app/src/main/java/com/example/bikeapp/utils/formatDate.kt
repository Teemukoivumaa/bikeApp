package com.example.bikeapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(date: Date): String {
    val format = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault())
    return format.format(date)
}