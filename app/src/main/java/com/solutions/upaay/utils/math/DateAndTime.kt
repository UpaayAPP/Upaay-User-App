package com.solutions.upaay.utils.math

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

fun formatCreatedAt(createdAt: String): String {
    // Parse the createdAt timestamp
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC") // Ensure UTC timezone

    val createdDate: Date? = try {
        dateFormat.parse(createdAt)
    } catch (e: Exception) {
        Log.d("crackinTAG", "formatCreatedAt: $e")
        e.printStackTrace()
        null
    }

    if (createdDate == null) return createdAt // Fallback if parsing fails

    val currentDate = Date()
    val diffInMillis = currentDate.time - createdDate.time
    val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

    return if (diffInDays == 0L) {
        "today"
    } else if (diffInDays <= 15) {
        "${diffInDays}d ago" // Show relative time if 15 days or less
    } else {
        // Format date as "2 Oct 2024"
        val outputFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
        "on "+outputFormat.format(createdDate)
    }
}
