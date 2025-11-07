package com.example.pawshearts.data.local


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pawshearts.data.model.UserData

/**
 * Lớp Database chính của ứng dụng.
 * @Database: Khai báo đây là một database của Room, chứa các bảng được định nghĩa trong `entities`.
 * `version`: Phiên bản của database. Khi bạn thay đổi cấu trúc bảng, bạn cần tăng version này.
 */
@Database(entities = [UserData::class], version = 1, exportSchema = false)
abstract class PawsHeartsDatabase : RoomDatabase() {

    // Cung cấp một hàm để các thành phần khác có thể lấy được DAO.
    abstract fun userDao(): UserDao

    // Companion object để tạo ra một instance duy nhất của database (Singleton pattern).
    companion object {
        @Volatile
        private var INSTANCE: PawsHeartsDatabase? = null

        fun getDatabase(context: Context): PawsHeartsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PawsHeartsDatabase::class.java,
                    "paws_hearts_database" // Tên file database sẽ được tạo trên thiết bị
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
