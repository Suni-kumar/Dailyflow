package com.example.data.local.dao.ai

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ai.AiMemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiMemoryDao {

    @Query("SELECT * FROM ai_memories ORDER BY updatedAt DESC")
    fun getAllMemories(): Flow<List<AiMemoryEntity>>
    
    @Query("SELECT * FROM ai_memories ORDER BY updatedAt DESC")
    suspend fun getAllMemoriesSync(): List<AiMemoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: AiMemoryEntity)

    @Query("DELETE FROM ai_memories WHERE id = :memoryId")
    suspend fun deleteMemory(memoryId: String)

    @Query("DELETE FROM ai_memories")
    suspend fun clearAllMemories()
}
