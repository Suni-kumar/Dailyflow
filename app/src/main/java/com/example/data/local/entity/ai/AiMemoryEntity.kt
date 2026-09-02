package com.example.data.local.entity.ai

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_memories")
data class AiMemoryEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val category: String,
    val createdAt: Long,
    val updatedAt: Long,
    val importance: Int = 1
)
