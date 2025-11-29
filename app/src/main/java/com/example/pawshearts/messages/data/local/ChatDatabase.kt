// com/example/pawshearts/messages/data/local/ChatDatabase.kt
package com.example.pawshearts.messages.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [MessageEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(MessageStatusConverters::class)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: ChatDatabase? = null

        fun getInstance(context: Context): ChatDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ChatDatabase::class.java,
                    "paws_hearts_chat.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
