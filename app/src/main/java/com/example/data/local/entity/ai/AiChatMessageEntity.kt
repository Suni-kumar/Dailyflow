package com.example.data.local.entity.ai

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ai_chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = AiChatSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class AiChatMessageEntity(
    @PrimaryKey
    val id: String,
    val sessionId: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: String,
    val createdAt: Long
)
