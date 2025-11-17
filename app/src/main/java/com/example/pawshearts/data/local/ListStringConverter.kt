package com.example.pawshearts.data.local

import androidx.room.TypeConverter

class ListStringConverter {
    @TypeConverter
    fun fromListString(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toListString(data: String): List<String> {
        // Handle empty string case to avoid a list with one empty element
        return if (data.isEmpty()) {
            emptyList()
        } else {
            data.split(',')
        }
    }
}
