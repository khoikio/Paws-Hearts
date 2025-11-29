package com.example.pawshearts.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pawshearts.data.model.UserData
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserData)

    @Query("SELECT * FROM user_profile WHERE userId = :userId")
    fun getUserByIdFlow(userId: String): Flow<UserData?>

    @Query("DELETE FROM user_profile WHERE userId = :userId")
    suspend fun deleteUserById(userId: String)

    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getLoggedInUserFlow(): Flow<UserData?>
}
