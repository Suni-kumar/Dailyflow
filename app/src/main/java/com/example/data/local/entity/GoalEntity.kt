package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.ItemCategory

@Entity(tableName = "goals")
data class GoalEntity(
  @PrimaryKey
  val id: String,
  val title: String,
  val description: String = "",
  val goalType: String = "LONG TERM",
  val category: ItemCategory = ItemCategory.LEARNING,
  val currentProgress: Int = 0,
  val targetProgress: Int = 100,
  val unit: String = "%",
  val deadline: String = "",
  val isCompleted: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)
