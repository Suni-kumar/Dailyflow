package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "goal_progress_records",
  foreignKeys = [
    ForeignKey(
      entity = GoalEntity::class,
      parentColumns = ["id"],
      childColumns = ["goalId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [
    Index(value = ["goalId"]),
    Index(value = ["recordedAt"])
  ]
)
data class GoalProgressEntity(
  @PrimaryKey
  val id: String,
  val goalId: String,
  val progressValue: Int,
  val delta: Int = 0,
  val recordedAt: Long = System.currentTimeMillis(),
  val note: String? = null
)
