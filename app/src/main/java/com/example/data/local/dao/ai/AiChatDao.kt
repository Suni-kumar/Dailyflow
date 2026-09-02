package com.example.data.local.dao.ai

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.local.entity.ai.AiChatMessageEntity
import com.example.data.local.entity.ai.AiChatSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiChatDao {

    @Query("SELECT * FROM ai_chat_sessions ORDER BY updatedAt DESC")
    fun getAllSessions(): Flow<List<AiChatSessionEntity>>

    @Query("SELECT * FROM ai_chat_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: String): AiChatSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: AiChatSessionEntity)

    @Query("UPDATE ai_chat_sessions SET title = :title, updatedAt = :updatedAt WHERE id = :sessionId")
    suspend fun updateSessionTitle(sessionId: String, title: String, updatedAt: Long)

    @Query("UPDATE ai_chat_sessions SET updatedAt = :updatedAt WHERE id = :sessionId")
    suspend fun updateSessionTimestamp(sessionId: String, updatedAt: Long)

    @Query("DELETE FROM ai_chat_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: String)

    @Query("DELETE FROM ai_chat_sessions")
    suspend fun clearAllSessions()

    @Query("SELECT * FROM ai_chat_messages WHERE sessionId = :sessionId ORDER BY createdAt ASC")
    fun getMessagesForSession(sessionId: String): Flow<List<AiChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: AiChatMessageEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<AiChatMessageEntity>)

    @Query("DELETE FROM ai_chat_messages WHERE sessionId = :sessionId")
    suspend fun deleteMessagesForSession(sessionId: String)

    @Query("DELETE FROM ai_chat_messages")
    suspend fun clearAllMessages()
    
    @Transaction
    suspend fun clearHistory() {
        clearAllMessages()
        clearAllSessions()
    }
}
