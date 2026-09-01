package com.example.data

import com.example.data.local.DayFlowDatabase
import com.example.data.local.dao.GoalDao
import com.example.data.local.dao.GoalProgressDao
import com.example.data.local.dao.HabitCompletionDao
import com.example.data.local.dao.HabitDao
import com.example.data.local.dao.TaskDao
import com.example.data.local.entity.GoalEntity
import com.example.data.local.entity.GoalProgressEntity
import com.example.data.local.entity.HabitCompletionEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.TaskEntity
import com.example.model.CalendarEventItem
import com.example.model.CoachInsight
import com.example.model.DailyProgressSummary
import com.example.model.GoalItem
import com.example.model.HabitItem
import com.example.model.InsightType
import com.example.model.ItemCategory
import com.example.model.TaskItem
import com.example.model.TaskPriority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class DayFlowRepository(
  private val taskDao: TaskDao? = null,
  private val habitDao: HabitDao? = null,
  private val habitCompletionDao: HabitCompletionDao? = null,
  private val goalDao: GoalDao? = null,
  private val goalProgressDao: GoalProgressDao? = null,
  private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

  // In-memory fallback if Room DAOs are not provided
  private val _inMemoryTasks = MutableStateFlow<List<TaskItem>>(
    listOf(
      TaskItem(
        id = "t1",
        title = "Morning Meditation",
        description = "Guided breathing and mindfulness",
        category = ItemCategory.MINDFULNESS,
        priority = TaskPriority.MEDIUM,
        time = "07:00 AM",
        dueDate = "Today",
        isCompleted = true,
        estimatedMinutes = 15
      ),
      TaskItem(
        id = "t2",
        title = "Deep Work Session",
        description = "High-concentration architecture planning and implementation",
        category = ItemCategory.WORK,
        priority = TaskPriority.HIGH,
        time = "09:00 AM",
        dueDate = "Today",
        isCompleted = false,
        estimatedMinutes = 120
      ),
      TaskItem(
        id = "t3",
        title = "Review Weekly Goals",
        description = "Evaluate quarterly key results and progress notes",
        category = ItemCategory.PERSONAL,
        priority = TaskPriority.LOW,
        time = "02:00 PM",
        dueDate = "Today",
        isCompleted = false,
        estimatedMinutes = 30
      )
    )
  )

  private val _inMemoryHabits = MutableStateFlow<List<HabitItem>>(
    listOf(
      HabitItem(
        id = "h1",
        title = "Hydration",
        category = ItemCategory.HEALTH,
        streakDays = 14,
        targetPerWeek = 7,
        completedToday = false,
        reminderTime = "08:00 AM"
      ),
      HabitItem(
        id = "h2",
        title = "Reading",
        category = ItemCategory.LEARNING,
        streakDays = 8,
        targetPerWeek = 7,
        completedToday = true,
        reminderTime = "09:00 PM"
      )
    )
  )

  private val _inMemoryGoals = MutableStateFlow<List<GoalItem>>(
    listOf(
      GoalItem(
        id = "g1",
        title = "Run 50km this month",
        category = ItemCategory.FITNESS,
        currentProgress = 32,
        targetProgress = 50,
        unit = "km",
        deadline = "In 12 days",
        isCompleted = false
      ),
      GoalItem(
        id = "g2",
        title = "Complete Kotlin Course",
        category = ItemCategory.LEARNING,
        currentProgress = 8,
        targetProgress = 12,
        unit = "modules",
        deadline = "In 18 days",
        isCompleted = false
      )
    )
  )

  private val _coachInsights = MutableStateFlow<List<CoachInsight>>(
    listOf(
      CoachInsight(
        id = "ci1",
        title = "Daily Focus Window",
        description = "You're operating at high momentum today. You have a prime focus window this afternoon to complete your high-priority items.",
        type = InsightType.MOTIVATION,
        timestamp = "8:00 AM"
      )
    )
  )
  val coachInsights: StateFlow<List<CoachInsight>> = _coachInsights.asStateFlow()

  // Reactive tasks stream
  val tasks: StateFlow<List<TaskItem>> = if (taskDao != null) {
    taskDao.getAllTasks()
      .map { list -> list.map { it.toTaskItem() } }
      .stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _inMemoryTasks.value
      )
  } else {
    _inMemoryTasks.asStateFlow()
  }

  // Reactive habits stream
  val habits: StateFlow<List<HabitItem>> = if (habitDao != null && habitCompletionDao != null) {
    combine(
      habitDao.getActiveHabits(),
      habitCompletionDao.getCompletionsForDate("Today")
    ) { habitEntities, todayCompletions ->
      val completedIds = todayCompletions.map { it.habitId }.toSet()
      habitEntities.map { entity ->
        entity.toHabitItem(isCompletedToday = completedIds.contains(entity.id))
      }
    }.stateIn(
      scope = coroutineScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = _inMemoryHabits.value
    )
  } else {
    _inMemoryHabits.asStateFlow()
  }

  // Reactive goals stream
  val goals: StateFlow<List<GoalItem>> = if (goalDao != null) {
    goalDao.getAllGoals()
      .map { list -> list.map { it.toGoalItem() } }
      .stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _inMemoryGoals.value
      )
  } else {
    _inMemoryGoals.asStateFlow()
  }

  // Derived Calendar Events from Tasks
  val calendarEvents: StateFlow<List<CalendarEventItem>> = tasks.map { taskList ->
    taskList.map { task ->
      CalendarEventItem(
        id = task.id,
        title = task.title,
        startTime = task.time,
        endTime = calculateEndTime(task.time, task.estimatedMinutes),
        category = task.category,
        date = task.dueDate,
        isCompleted = task.isCompleted
      )
    }
  }.stateIn(
    scope = coroutineScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  // Mutators
  fun toggleTask(taskId: String) {
    coroutineScope.launch {
      if (taskDao != null) {
        val existing = taskDao.getTaskById(taskId)
        if (existing != null) {
          taskDao.updateTaskCompletion(taskId, !existing.isCompleted)
        }
      } else {
        _inMemoryTasks.value = _inMemoryTasks.value.map { task ->
          if (task.id == taskId) task.copy(isCompleted = !task.isCompleted) else task
        }
      }
    }
  }

  fun addTask(task: TaskItem) {
    coroutineScope.launch {
      if (taskDao != null) {
        taskDao.insertTask(task.toEntity())
      } else {
        _inMemoryTasks.value = listOf(task) + _inMemoryTasks.value
      }
    }
  }

  fun deleteTask(taskId: String) {
    coroutineScope.launch {
      if (taskDao != null) {
        taskDao.deleteTaskById(taskId)
      } else {
        _inMemoryTasks.value = _inMemoryTasks.value.filter { it.id != taskId }
      }
    }
  }

  fun toggleHabit(habitId: String, date: String = "Today") {
    coroutineScope.launch {
      if (habitDao != null && habitCompletionDao != null) {
        val habit = habitDao.getHabitById(habitId) ?: return@launch
        val completion = habitCompletionDao.getCompletion(habitId, date)
        if (completion != null) {
          habitCompletionDao.deleteCompletion(habitId, date)
          val newStreak = (habit.streakDays - 1).coerceAtLeast(0)
          habitDao.updateHabitStreak(habitId, newStreak)
        } else {
          habitCompletionDao.insertCompletion(
            HabitCompletionEntity(
              id = UUID.randomUUID().toString(),
              habitId = habitId,
              completionDate = date
            )
          )
          val newStreak = habit.streakDays + 1
          habitDao.updateHabitStreak(habitId, newStreak)
        }
      } else {
        _inMemoryHabits.value = _inMemoryHabits.value.map { habit ->
          if (habit.id == habitId) {
            val newCompleted = !habit.completedToday
            val newStreak = if (newCompleted) habit.streakDays + 1 else (habit.streakDays - 1).coerceAtLeast(0)
            habit.copy(completedToday = newCompleted, streakDays = newStreak)
          } else {
            habit
          }
        }
      }
    }
  }

  fun updateGoalProgress(goalId: String, increment: Int) {
    coroutineScope.launch {
      if (goalDao != null && goalProgressDao != null) {
        val goal = goalDao.getGoalById(goalId) ?: return@launch
        val newProgress = (goal.currentProgress + increment).coerceIn(0, goal.targetProgress)
        val isCompleted = newProgress >= goal.targetProgress
        goalDao.updateGoalProgress(goalId, newProgress, isCompleted)

        // Record history entry
        goalProgressDao.insertProgressRecord(
          GoalProgressEntity(
            id = UUID.randomUUID().toString(),
            goalId = goalId,
            progressValue = newProgress,
            delta = increment
          )
        )
      } else {
        _inMemoryGoals.value = _inMemoryGoals.value.map { goal ->
          if (goal.id == goalId) {
            val newProgress = (goal.currentProgress + increment).coerceIn(0, goal.targetProgress)
            goal.copy(
              currentProgress = newProgress,
              isCompleted = newProgress >= goal.targetProgress
            )
          } else {
            goal
          }
        }
      }
    }
  }

  fun addGoal(goal: GoalItem) {
    coroutineScope.launch {
      if (goalDao != null) {
        goalDao.insertGoal(goal.toEntity())
      } else {
        _inMemoryGoals.value = _inMemoryGoals.value + goal
      }
    }
  }

  fun addCoachInsight(insight: CoachInsight) {
    _coachInsights.value = listOf(insight) + _coachInsights.value
  }

  fun getProgressSummary(): DailyProgressSummary {
    val taskList = tasks.value
    val habitList = habits.value
    val completedCount = taskList.count { it.isCompleted }
    val completedHabits = habitList.count { it.completedToday }
    val focusMins = taskList.filter { it.isCompleted }.sumOf { it.estimatedMinutes }
    val maxStreak = habitList.maxOfOrNull { it.streakDays } ?: 0

    return DailyProgressSummary(
      totalTasks = taskList.size,
      completedTasks = completedCount,
      habitsCompleted = completedHabits,
      totalHabits = habitList.size,
      focusMinutes = focusMins,
      currentStreak = maxStreak
    )
  }

  companion object {
    fun fromDatabase(database: DayFlowDatabase, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): DayFlowRepository {
      return DayFlowRepository(
        taskDao = database.taskDao(),
        habitDao = database.habitDao(),
        habitCompletionDao = database.habitCompletionDao(),
        goalDao = database.goalDao(),
        goalProgressDao = database.goalProgressDao(),
        coroutineScope = scope
      )
    }

    private fun calculateEndTime(startTime: String, durationMinutes: Int): String {
      return try {
        val parts = startTime.trim().split(" ")
        val timeParts = parts[0].split(":")
        var hour = timeParts[0].toInt()
        var minute = timeParts[1].toInt()
        val amPm = if (parts.size > 1) parts[1].uppercase() else "AM"

        if (amPm == "PM" && hour < 12) hour += 12
        if (amPm == "AM" && hour == 12) hour = 0

        var totalMinutes = hour * 60 + minute + durationMinutes
        val endHour24 = (totalMinutes / 60) % 24
        val endMinute = totalMinutes % 60

        val endAmPm = if (endHour24 >= 12) "PM" else "AM"
        val endHour12 = when {
          endHour24 == 0 -> 12
          endHour24 > 12 -> endHour24 - 12
          else -> endHour24
        }
        String.format("%02d:%02d %s", endHour12, endMinute, endAmPm)
      } catch (e: Exception) {
        startTime
      }
    }
  }
}

// Mapper extension functions
fun TaskEntity.toTaskItem(): TaskItem {
  return TaskItem(
    id = id,
    title = title,
    description = description,
    category = category,
    priority = priority,
    time = startTime ?: "09:00 AM",
    dueDate = dueDate,
    isCompleted = isCompleted,
    estimatedMinutes = estimatedMinutes
  )
}

fun TaskItem.toEntity(): TaskEntity {
  return TaskEntity(
    id = id,
    title = title,
    description = description,
    dueDate = dueDate,
    startTime = time,
    category = category,
    priority = priority,
    isCompleted = isCompleted,
    estimatedMinutes = estimatedMinutes
  )
}

fun HabitEntity.toHabitItem(isCompletedToday: Boolean): HabitItem {
  return HabitItem(
    id = id,
    title = name,
    category = category,
    streakDays = streakDays,
    targetPerWeek = targetPerWeek,
    completedToday = isCompletedToday,
    reminderTime = reminderTime ?: "08:00 AM"
  )
}

fun HabitItem.toEntity(): HabitEntity {
  return HabitEntity(
    id = id,
    name = title,
    category = category,
    scheduleFrequency = "DAILY",
    targetPerWeek = targetPerWeek,
    streakDays = streakDays,
    reminderTime = reminderTime,
    isActive = true
  )
}

fun GoalEntity.toGoalItem(): GoalItem {
  return GoalItem(
    id = id,
    title = title,
    category = category,
    currentProgress = currentProgress,
    targetProgress = targetProgress,
    unit = unit,
    deadline = deadline,
    isCompleted = isCompleted
  )
}

fun GoalItem.toEntity(): GoalEntity {
  return GoalEntity(
    id = id,
    title = title,
    description = "",
    category = category,
    currentProgress = currentProgress,
    targetProgress = targetProgress,
    unit = unit,
    deadline = deadline,
    isCompleted = isCompleted
  )
}
