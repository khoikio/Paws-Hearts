// com/example/pawshearts/messages/data/local/MessageDao.kt
package com.example.pawshearts.messages.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query("""
        SELECT * FROM messages 
        WHERE threadId = :threadId 
        ORDER BY sentAt ASC
    """)
    fun observeMessagesInThread(threadId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMessages(messages: List<MessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMessage(message: MessageEntity)

    @Update
    suspend fun updateMessage(message: MessageEntity)

    /**
     * Lấy tin nhắn cuối của từng thread để hiển thị ở màn hình danh sách hội thoại.
     */
    @Query("""
        SELECT m.* FROM messages m
        INNER JOIN (
            SELECT threadId, MAX(sentAt) AS maxSentAt
            FROM messages
            GROUP BY threadId
        ) last ON m.threadId = last.threadId AND m.sentAt = last.maxSentAt
        ORDER BY m.sentAt DESC
    """)
    fun observeLastMessagesPerThread(): Flow<List<MessageEntity>>
}
