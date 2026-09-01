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
  val streakDays: Int = 0,
  val targetPerWeek: Int = 7,
  val dailyTarget: Int = 1,
  val unit: String = "",
  val currentProgress: Int = 0,
  val completedToday: Boolean = false,
  val reminderTime: String = "08:00 AM"
) {
  val progressFraction: Float
    get() = if (dailyTarget > 0) (currentProgress.toFloat() / dailyTarget.toFloat()).coerceIn(0f, 1f) else if (completedToday) 1f else 0f
}

data class GoalItem(
  val id: String,
  val title: String,
  val description: String = "",
  val goalType: String = "LONG TERM",
  val category: ItemCategory = ItemCategory.LEARNING,
  val currentProgress: Int = 0,
  val targetProgress: Int = 100,
  val unit: String = "%",
  val deadline: String = "180d left",
  val isCompleted: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
) {
  val progressPercentage: Int
    get() = if (targetProgress > 0) ((currentProgress.toFloat() / targetProgress.toFloat()) * 100).toInt().coerceIn(0, 100) else 0

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

enum class StatsTimeRange(val days: Int, val label: String) {
  DAYS_7(7, "7 Days"),
  DAYS_14(14, "14 Days"),
  DAYS_30(30, "30 Days")
}

data class DailyActivityStat(
  val dateKey: String,
  val dayLabel: String,
  val dayNumber: String,
  val completedTasks: Int,
  val totalTasks: Int,
  val focusMinutes: Int,
  val isCurrentDay: Boolean,
  val heightFraction: Float,
  val tooltipValue: String = ""
)

data class CategoryStat(
  val category: ItemCategory,
  val completedCount: Int,
  val totalCount: Int,
  val percentage: Int
)

data class StatisticsData(
  val timeRange: StatsTimeRange = StatsTimeRange.DAYS_7,
  val tasksCompleted: Int = 0,
  val tasksPlanned: Int = 0,
  val completionRate: Int = 0,
  val currentStreak: Int = 0,
  val bestStreak: Int = 0,
  val totalFocusMinutes: Int = 0,
  val plannedFocusMinutes: Int = 0,
  val dailyStats: List<DailyActivityStat> = emptyList(),
  val categoryStats: List<CategoryStat> = emptyList(),
  val recentStreakDays: List<Boolean> = emptyList(),
  val hasAnyActivity: Boolean = false
)
