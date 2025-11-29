package com.example.pawshearts.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pawshearts.data.model.UserData

@Database(
    entities = [
        UserData::class
    ],
    version = 9,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListStringConverter::class)
abstract class PawsHeartsDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: PawsHeartsDatabase? = null

        fun getDatabase(context: Context): PawsHeartsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PawsHeartsDatabase::class.java,
                    "paws_hearts_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
