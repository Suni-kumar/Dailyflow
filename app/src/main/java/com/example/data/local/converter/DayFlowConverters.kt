package com.example.data.local.converter

import androidx.room.TypeConverter
import com.example.model.InsightType
import com.example.model.ItemCategory
import com.example.model.TaskPriority

class DayFlowConverters {

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
