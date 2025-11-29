// com/example/pawshearts/messages/data/local/MessageStatusConverters.kt
package com.example.pawshearts.messages.data.local

import androidx.room.TypeConverter
import com.example.pawshearts.messages.model.MessageStatus

class MessageStatusConverters {

    @TypeConverter
    fun fromStatus(status: MessageStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): MessageStatus = MessageStatus.valueOf(value)
}
