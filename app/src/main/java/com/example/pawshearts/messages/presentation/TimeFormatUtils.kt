package com.example.pawshearts.messages.presentation

import java.text.SimpleDateFormat
import java.util.*

object TimeFormatUtils {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

    fun formatTime(timestamp: Long): String {
        val now = System.currentTimeMillis()

        val diff = now - timestamp
        val oneDay = 24 * 60 * 60 * 1000L

        return if (diff < oneDay) {
            timeFormat.format(Date(timestamp))     // Ví dụ: 19:42
        } else {
            dateFormat.format(Date(timestamp))     // Ví dụ: 15/11
        }
    }
}
