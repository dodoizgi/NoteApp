package com.task.noteapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    const val P2 = "dd.MM.yyyy"
    const val P3 = "yyyy-MM-dd"

    fun parse(source: String?, pattern: String): Date? {
        return try {
            val date = SimpleDateFormat(pattern, Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.parse(source!!)
            date
        } catch (e: Exception) {
            null
        }
    }

    fun format(date: Date?, pattern: String): String? {
        return if (date != null) {
            SimpleDateFormat(pattern, Locale.getDefault()).format(date)
        } else {
            null
        }
    }
}
