package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "habit_completions",
  foreignKeys = [
    ForeignKey(
      entity = HabitEntity::class,
      parentColumns = ["id"],
      childColumns = ["habitId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [
    Index(value = ["habitId"]),
    Index(value = ["completionDate"]),
    Index(value = ["habitId", "completionDate"], unique = true)
  ]
)
data class HabitCompletionEntity(
  @PrimaryKey
  val id: String,
  val habitId: String,
  val completionDate: String, // e.g. "2026-09-01"
  val completedAt: Long = System.currentTimeMillis(),
  val progressValue: Int = 1,
  val isCompleted: Boolean = true,
  val notes: String? = null
)
