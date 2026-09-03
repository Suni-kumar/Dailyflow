package com.example.data.local.converter

import androidx.room.TypeConverter
import com.example.model.InsightType
import com.example.model.ItemCategory
import com.example.model.TaskPriority
import com.example.model.TaskStatus

class DayFlowConverters {

  @TypeConverter
  fun fromTaskStatus(status: TaskStatus?): String? {
    return status?.name
  }

  @TypeConverter
  fun toTaskStatus(value: String?): TaskStatus? {
    return value?.let {
      try {
        TaskStatus.valueOf(it)
      } catch (e: Exception) {
        TaskStatus.PENDING
      }
    }
  }

  @TypeConverter
  fun fromItemCategory(category: ItemCategory?): String? {
    return category?.name
  }

  @TypeConverter
  fun toItemCategory(value: String?): ItemCategory? {
    return value?.let {
      try {
        ItemCategory.valueOf(it)
      } catch (e: Exception) {
        ItemCategory.WORK
      }
    }
  }

  @TypeConverter
  fun fromTaskPriority(priority: TaskPriority?): String? {
    return priority?.name
  }

  @TypeConverter
  fun toTaskPriority(value: String?): TaskPriority? {
    return value?.let {
      try {
        TaskPriority.valueOf(it)
      } catch (e: Exception) {
        TaskPriority.MEDIUM
      }
    }
  }

  @TypeConverter
  fun fromInsightType(type: InsightType?): String? {
    return type?.name
  }

  @TypeConverter
  fun toInsightType(value: String?): InsightType? {
    return value?.let {
      try {
        InsightType.valueOf(it)
      } catch (e: Exception) {
        InsightType.ADVICE
      }
    }
  }
}
