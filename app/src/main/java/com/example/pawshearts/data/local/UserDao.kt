package com.example.pawshearts.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pawshearts.data.model.UserData
import kotlinx.coroutines.flow.Flow

/**
 * Giao diện này chứa các lệnh để truy cập dữ liệu Người Dùng trong Room.
 */
@Dao
interface UserDao {
    /**
     * Lấy thông tin người dùng bằng ID và trả về một Flow.
     * Flow sẽ tự động phát ra dữ liệu mới mỗi khi người dùng này được cập nhật trong Room.
     */
    @Query("SELECT * FROM user_profile WHERE userId = :id")
    fun getUserById(id: String): Flow<UserData?>

    /**
     * Chèn hoặc Cập nhật một người dùng.
     * Nếu người dùng đã tồn tại (dựa trên PrimaryKey), nó sẽ được thay thế.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserData)

    /**
     * Xóa một người dùng khỏi cơ sở dữ liệu cục bộ bằng ID.
     * Dùng khi người dùng đăng xuất để bảo mật thông tin.
     */
    @Query("DELETE FROM user_profile WHERE userId = :id")
    suspend fun deleteUserById(id: String)
}
