package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.ItemCategory
import com.example.model.TaskPriority

@Entity(tableName = "tasks")
data class TaskEntity(
  @PrimaryKey
  val id: String,
  val title: String,
  val description: String = "",
  val dueDate: String = "Today",
  val startTime: String? = null,
  val endTime: String? = null,
  val category: ItemCategory = ItemCategory.WORK,
  val priority: TaskPriority = TaskPriority.MEDIUM,
  val isCompleted: Boolean = false,
  val estimatedMinutes: Int = 30,
  val isRecurring: Boolean = false,
  val recurringPattern: String? = null,
  val createdAt: Long = System.currentTimeMillis()
)
