package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.ItemCategory
import com.example.model.TaskPriority
import com.example.model.TaskStatus

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
  val status: TaskStatus = TaskStatus.PENDING,
  val isCompleted: Boolean = false,
  val exceptionReason: String? = null,
  val estimatedMinutes: Int = 30,
  val isRecurring: Boolean = false,
  val recurringPattern: String? = null,
  val createdAt: Long = System.currentTimeMillis()
)
