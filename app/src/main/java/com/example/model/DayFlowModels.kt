package com.example.model

enum class TaskPriority {
  LOW,
  MEDIUM,
  HIGH
}

enum class ItemCategory(val displayName: String, val colorHex: Long) {
  WORK("Work", 0xFF3B82F6),
  PERSONAL("Personal", 0xFF8B5CF6),
  HEALTH("Health", 0xFF10B981),
  LEARNING("Learning", 0xFFF59E0B),
  FITNESS("Fitness", 0xFFEF4444),
  MINDFULNESS("Mind", 0xFF06B6D4)
}

data class TaskItem(
  val id: String,
  val title: String,
  val description: String = "",
  val category: ItemCategory = ItemCategory.WORK,
  val priority: TaskPriority = TaskPriority.MEDIUM,
  val time: String = "09:00 AM",
  val dueDate: String = "Today",
  val isCompleted: Boolean = false,
  val estimatedMinutes: Int = 30
)

data class HabitItem(
  val id: String,
  val title: String,
  val category: ItemCategory = ItemCategory.HEALTH,
  val streakDays: Int = 1,
  val targetPerWeek: Int = 7,
  val completedToday: Boolean = false,
  val reminderTime: String = "08:00 AM"
)

data class GoalItem(
  val id: String,
  val title: String,
  val category: ItemCategory = ItemCategory.LEARNING,
  val currentProgress: Int = 0,
  val targetProgress: Int = 100,
  val unit: String = "%",
  val deadline: String = "In 30 days",
  val isCompleted: Boolean = false
) {
  val progressFraction: Float
    get() = if (targetProgress > 0) (currentProgress.toFloat() / targetProgress.toFloat()).coerceIn(0f, 1f) else 0f
}

data class CalendarEventItem(
  val id: String,
  val title: String,
  val startTime: String,
  val endTime: String,
  val category: ItemCategory,
  val date: String,
  val isCompleted: Boolean = false
)

data class CoachInsight(
  val id: String,
  val title: String,
  val description: String,
  val type: InsightType = InsightType.ADVICE,
  val timestamp: String = "Just now"
)

enum class InsightType {
  ADVICE,
  MOTIVATION,
  HABIT_ALERT,
  PRODUCTIVITY_TIP
}

data class DailyProgressSummary(
  val totalTasks: Int,
  val completedTasks: Int,
  val habitsCompleted: Int,
  val totalHabits: Int,
  val focusMinutes: Int,
  val currentStreak: Int
) {
  val completionRate: Float
    get() = if (totalTasks > 0) (completedTasks.toFloat() / totalTasks.toFloat()).coerceIn(0f, 1f) else 0f
}
