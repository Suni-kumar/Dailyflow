package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.ItemCategory

@Entity(tableName = "habits")
data class HabitEntity(
  @PrimaryKey
  val id: String,
  val name: String,
  val category: ItemCategory = ItemCategory.HEALTH,
  val scheduleFrequency: String = "DAILY",
  val targetPerWeek: Int = 7,
  val dailyTarget: Int = 1,
  val unit: String = "",
  val streakDays: Int = 0,
  val reminderTime: String? = null,
  val isActive: Boolean = true,
  val createdAt: Long = System.currentTimeMillis()
)
