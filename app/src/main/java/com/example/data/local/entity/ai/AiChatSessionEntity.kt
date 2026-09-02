package com.example.data.local.entity.ai

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_chat_sessions")
data class AiChatSessionEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long
)
