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
import com.example.model.CategoryStat
import com.example.model.CoachInsight
import com.example.model.DailyActivityStat
import com.example.model.DailyProgressSummary
import com.example.model.GoalItem
import com.example.model.HabitItem
import com.example.model.InsightType
import com.example.model.ItemCategory
import com.example.model.StatisticsData
import com.example.model.StatsTimeRange
import com.example.model.TaskItem
import com.example.model.TaskPriority
import com.example.util.DateUtils
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
  private val database: DayFlowDatabase? = null,
  private val preferencesManager: com.example.data.local.UserPreferencesManager? = null,
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
        dueDate = com.example.util.DateUtils.getTodayDateKey(),
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
        dueDate = com.example.util.DateUtils.getTodayDateKey(),
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
        dueDate = com.example.util.DateUtils.getTodayDateKey(),
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
        dailyTarget = 5,
        unit = "L",
        currentProgress = 3,
        completedToday = false,
        reminderTime = "08:00 AM"
      ),
      HabitItem(
        id = "h2",
        title = "Reading",
        category = ItemCategory.LEARNING,
        streakDays = 8,
        targetPerWeek = 7,
        dailyTarget = 30,
        unit = "min",
        currentProgress = 30,
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

  // Reactive all tasks stream
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

  // Get tasks filtered by specific date
  fun getTasksForDate(date: String): Flow<List<TaskItem>> {
    return if (taskDao != null) {
      taskDao.getAllTasks().map { list ->
        list.filter { it.dueDate == date || (date == com.example.util.DateUtils.getTodayDateKey() && it.dueDate == "Today") }
          .map { it.toTaskItem() }
      }
    } else {
      _inMemoryTasks.map { list ->
        list.filter { it.dueDate == date || (date == com.example.util.DateUtils.getTodayDateKey() && it.dueDate == "Today") }
      }
    }
  }

  // Reactive habits stream for a given date
  fun getHabitsForDate(date: String): Flow<List<HabitItem>> {
    return if (habitDao != null && habitCompletionDao != null) {
      combine(
        habitDao.getActiveHabits(),
        habitCompletionDao.getCompletionsForDate(date)
      ) { habitEntities, completions ->
        val completionMap = completions.associateBy { it.habitId }
        habitEntities.map { entity ->
          val completion = completionMap[entity.id]
          val progress = completion?.progressValue ?: 0
          val isDone = completion?.isCompleted ?: (progress >= entity.dailyTarget && entity.dailyTarget > 0)
          entity.toHabitItem(currentProgress = progress, isCompletedToday = isDone)
        }
      }
    } else {
      _inMemoryHabits.asStateFlow()
    }
  }

  // Reactive habits stream for today
  val habits: StateFlow<List<HabitItem>> = getHabitsForDate(com.example.util.DateUtils.getTodayDateKey())
    .stateIn(
      scope = coroutineScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = _inMemoryHabits.value
    )

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

  fun updateTask(task: TaskItem) {
    coroutineScope.launch {
      if (taskDao != null) {
        taskDao.updateTask(task.toEntity())
      } else {
        _inMemoryTasks.value = _inMemoryTasks.value.map { if (it.id == task.id) task else it }
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

  fun toggleHabit(habitId: String, date: String = com.example.util.DateUtils.getTodayDateKey()) {
    coroutineScope.launch {
      if (habitDao != null && habitCompletionDao != null) {
        val habit = habitDao.getHabitById(habitId) ?: return@launch
        val completion = habitCompletionDao.getCompletion(habitId, date)
        if (completion != null && completion.isCompleted) {
          habitCompletionDao.deleteCompletion(habitId, date)
          val newStreak = (habit.streakDays - 1).coerceAtLeast(0)
          habitDao.updateHabitStreak(habitId, newStreak)
        } else {
          habitCompletionDao.insertCompletion(
            HabitCompletionEntity(
              id = UUID.randomUUID().toString(),
              habitId = habitId,
              completionDate = date,
              progressValue = habit.dailyTarget,
              isCompleted = true
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
            val newProgress = if (newCompleted) habit.dailyTarget else 0
            habit.copy(completedToday = newCompleted, streakDays = newStreak, currentProgress = newProgress)
          } else {
            habit
          }
        }
      }
    }
  }

  fun updateHabitProgress(habitId: String, date: String, newProgress: Int) {
    coroutineScope.launch {
      if (habitDao != null && habitCompletionDao != null) {
        val habit = habitDao.getHabitById(habitId) ?: return@launch
        val validProgress = newProgress.coerceAtLeast(0)
        val isDone = validProgress >= habit.dailyTarget && habit.dailyTarget > 0

        if (validProgress == 0 && !isDone) {
          habitCompletionDao.deleteCompletion(habitId, date)
        } else {
          habitCompletionDao.insertCompletion(
            HabitCompletionEntity(
              id = UUID.randomUUID().toString(),
              habitId = habitId,
              completionDate = date,
              progressValue = validProgress,
              isCompleted = isDone
            )
          )
        }
      } else {
        _inMemoryHabits.value = _inMemoryHabits.value.map { habit ->
          if (habit.id == habitId) {
            val isDone = newProgress >= habit.dailyTarget && habit.dailyTarget > 0
            habit.copy(currentProgress = newProgress, completedToday = isDone)
          } else {
            habit
          }
        }
      }
    }
  }

  fun addHabit(habit: HabitItem) {
    coroutineScope.launch {
      if (habitDao != null) {
        habitDao.insertHabit(habit.toEntity())
      } else {
        _inMemoryHabits.value = _inMemoryHabits.value + habit
      }
    }
  }

  fun deleteHabit(habitId: String) {
    coroutineScope.launch {
      if (habitDao != null) {
        habitDao.deleteHabitById(habitId)
      } else {
        _inMemoryHabits.value = _inMemoryHabits.value.filter { it.id != habitId }
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

  fun setGoalProgress(goalId: String, newProgress: Int) {
    coroutineScope.launch {
      if (goalDao != null && goalProgressDao != null) {
        val goal = goalDao.getGoalById(goalId) ?: return@launch
        val validProgress = newProgress.coerceIn(0, goal.targetProgress)
        val isCompleted = validProgress >= goal.targetProgress
        goalDao.updateGoalProgress(goalId, validProgress, isCompleted)

        goalProgressDao.insertProgressRecord(
          GoalProgressEntity(
            id = UUID.randomUUID().toString(),
            goalId = goalId,
            progressValue = validProgress,
            delta = validProgress - goal.currentProgress
          )
        )
      } else {
        _inMemoryGoals.value = _inMemoryGoals.value.map { goal ->
          if (goal.id == goalId) {
            val valid = newProgress.coerceIn(0, goal.targetProgress)
            goal.copy(currentProgress = valid, isCompleted = valid >= goal.targetProgress)
          } else {
            goal
          }
        }
      }
    }
  }

  fun toggleGoalCompletion(goalId: String) {
    coroutineScope.launch {
      if (goalDao != null) {
        val goal = goalDao.getGoalById(goalId) ?: return@launch
        val nextCompleted = !goal.isCompleted
        val nextProgress = if (nextCompleted) goal.targetProgress else goal.currentProgress.coerceAtMost(goal.targetProgress - 1).coerceAtLeast(0)
        goalDao.updateGoalProgress(goalId, nextProgress, nextCompleted)
      } else {
        _inMemoryGoals.value = _inMemoryGoals.value.map { goal ->
          if (goal.id == goalId) {
            val nextCompleted = !goal.isCompleted
            val nextProgress = if (nextCompleted) goal.targetProgress else goal.currentProgress.coerceAtMost(goal.targetProgress - 1).coerceAtLeast(0)
            goal.copy(isCompleted = nextCompleted, currentProgress = nextProgress)
          } else {
            goal
          }
        }
      }
    }
  }

  fun updateGoal(goal: GoalItem) {
    coroutineScope.launch {
      if (goalDao != null) {
        goalDao.updateGoal(goal.toEntity())
      } else {
        _inMemoryGoals.value = _inMemoryGoals.value.map { if (it.id == goal.id) goal else it }
      }
    }
  }

  fun deleteGoal(goalId: String) {
    coroutineScope.launch {
      if (goalDao != null) {
        goalDao.deleteGoalById(goalId)
      } else {
        _inMemoryGoals.value = _inMemoryGoals.value.filter { it.id != goalId }
      }
    }
  }

  fun addGoal(goal: GoalItem) {
    coroutineScope.launch {
      if (goalDao != null) {
        goalDao.insertGoal(goal.toEntity())
      } else {
        _inMemoryGoals.value = listOf(goal) + _inMemoryGoals.value
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

  fun calculateStatistics(
    timeRange: StatsTimeRange,
    taskList: List<TaskItem>,
    habitList: List<HabitItem>
  ): StatisticsData {
    val todayKey = DateUtils.getTodayDateKey()
    val dateKeys = DateUtils.getLastNDaysKeys(timeRange.days)
    val dateKeySet = dateKeys.toSet()

    // Normalize task dates so "Today" or blank string is mapped to today's dateKey
    val tasksWithNormalizedDate = taskList.map { task ->
      val normalized = if (task.dueDate == "Today" || task.dueDate.isBlank()) todayKey else task.dueDate
      task.copy(dueDate = normalized)
    }

    val tasksByDate = tasksWithNormalizedDate.groupBy { it.dueDate }

    // In-range tasks
    val inRangeTasks = tasksWithNormalizedDate.filter { it.dueDate in dateKeySet }
    val tasksCompleted = inRangeTasks.count { it.isCompleted }
    val tasksPlanned = inRangeTasks.size
    val completionRate = if (tasksPlanned > 0) {
      ((tasksCompleted.toFloat() / tasksPlanned.toFloat()) * 100).toInt()
    } else {
      0
    }
    val totalFocusMinutes = inRangeTasks.filter { it.isCompleted }.sumOf { it.estimatedMinutes }
    val plannedFocusMinutes = inRangeTasks.sumOf { it.estimatedMinutes }

    // Find max completed tasks on any day in range for proportional bar heights
    val maxCompletedInDay = dateKeys.maxOfOrNull { tasksByDate[it]?.count { t -> t.isCompleted } ?: 0 } ?: 0

    val dailyStats = dateKeys.map { dKey ->
      val dayTasks = tasksByDate[dKey] ?: emptyList()
      val completed = dayTasks.count { it.isCompleted }
      val total = dayTasks.size
      val focus = dayTasks.filter { it.isCompleted }.sumOf { it.estimatedMinutes }
      val isToday = dKey == todayKey

      val heightFraction = when {
        maxCompletedInDay > 0 && completed > 0 -> {
          (completed.toFloat() / maxCompletedInDay.toFloat()).coerceIn(0.18f, 1f)
        }
        completed > 0 -> 0.5f
        total > 0 -> 0.10f
        else -> 0.05f
      }

      val tooltip = when {
        focus > 0 -> DateUtils.formatFocusMinutes(focus)
        completed > 0 -> "$completed done"
        total > 0 -> "$total planned"
        else -> "No tasks"
      }

      DailyActivityStat(
        dateKey = dKey,
        dayLabel = DateUtils.getShortDayName(dKey),
        dayNumber = DateUtils.getDayOfMonthString(dKey),
        completedTasks = completed,
        totalTasks = total,
        focusMinutes = focus,
        isCurrentDay = isToday,
        heightFraction = heightFraction,
        tooltipValue = tooltip
      )
    }

    // Streak calculation (days with completed tasks)
    val completedDates = tasksByDate.filter { (_, list) -> list.any { it.isCompleted } }.keys.toSet()

    // Calculate current streak
    val cal = java.util.Calendar.getInstance()
    val iso = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
    var currentStreakCount = 0
    val isTodayCompleted = completedDates.contains(todayKey)
    if (isTodayCompleted) {
      currentStreakCount++
      cal.add(java.util.Calendar.DAY_OF_MONTH, -1)
      while (completedDates.contains(iso.format(cal.time))) {
        currentStreakCount++
        cal.add(java.util.Calendar.DAY_OF_MONTH, -1)
      }
    } else {
      // Check if yesterday was completed
      cal.add(java.util.Calendar.DAY_OF_MONTH, -1)
      if (completedDates.contains(iso.format(cal.time))) {
        currentStreakCount++
        cal.add(java.util.Calendar.DAY_OF_MONTH, -1)
        while (completedDates.contains(iso.format(cal.time))) {
          currentStreakCount++
          cal.add(java.util.Calendar.DAY_OF_MONTH, -1)
        }
      }
    }

    // Also consider max habit streak
    val habitMaxStreak = habitList.maxOfOrNull { it.streakDays } ?: 0
    val finalCurrentStreak = maxOf(currentStreakCount, if (habitList.any { it.completedToday }) habitMaxStreak else 0)
    val finalBestStreak = maxOf(finalCurrentStreak, habitMaxStreak, currentStreakCount)

    // Recent 5 days completion dots
    val recent5Keys = DateUtils.getLastNDaysKeys(5)
    val recentStreakDays = recent5Keys.map { completedDates.contains(it) }

    // Category breakdown
    val categorySourceTasks = if (inRangeTasks.isNotEmpty()) inRangeTasks else tasksWithNormalizedDate
    val categoryStats = if (categorySourceTasks.isNotEmpty()) {
      categorySourceTasks.groupBy { it.category }
        .map { (cat, catTasks) ->
          val done = catTasks.count { it.isCompleted }
          val pct = ((catTasks.size.toFloat() / categorySourceTasks.size.toFloat()) * 100).toInt()
          CategoryStat(
            category = cat,
            completedCount = done,
            totalCount = catTasks.size,
            percentage = pct
          )
        }
        .sortedByDescending { it.totalCount }
    } else {
      emptyList()
    }

    val hasAnyActivity = tasksPlanned > 0 || tasksCompleted > 0 || taskList.isNotEmpty()

    return StatisticsData(
      timeRange = timeRange,
      tasksCompleted = tasksCompleted,
      tasksPlanned = tasksPlanned,
      completionRate = completionRate,
      currentStreak = finalCurrentStreak,
      bestStreak = finalBestStreak,
      totalFocusMinutes = totalFocusMinutes,
      plannedFocusMinutes = plannedFocusMinutes,
      dailyStats = dailyStats,
      categoryStats = categoryStats,
      recentStreakDays = recentStreakDays,
      hasAnyActivity = hasAnyActivity
    )
  }

  suspend fun exportBackupJson(): String {
    return com.example.data.local.DayFlowBackupManager.exportBackupJson(database, preferencesManager)
  }

  suspend fun importBackupJson(jsonString: String): Result<com.example.data.local.ImportResultSummary> {
    return com.example.data.local.DayFlowBackupManager.importBackupJson(jsonString, database, preferencesManager)
  }

  fun getPreferencesManager(): com.example.data.local.UserPreferencesManager? = preferencesManager

  companion object {
    fun fromDatabase(
      database: DayFlowDatabase,
      preferencesManager: com.example.data.local.UserPreferencesManager? = null,
      scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    ): DayFlowRepository {
      return DayFlowRepository(
        taskDao = database.taskDao(),
        habitDao = database.habitDao(),
        habitCompletionDao = database.habitCompletionDao(),
        goalDao = database.goalDao(),
        goalProgressDao = database.goalProgressDao(),
        database = database,
        preferencesManager = preferencesManager,
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

fun HabitEntity.toHabitItem(currentProgress: Int = 0, isCompletedToday: Boolean = false): HabitItem {
  return HabitItem(
    id = id,
    title = name,
    category = category,
    streakDays = streakDays,
    targetPerWeek = targetPerWeek,
    dailyTarget = dailyTarget,
    unit = unit,
    currentProgress = currentProgress,
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
    dailyTarget = dailyTarget,
    unit = unit,
    streakDays = streakDays,
    reminderTime = reminderTime,
    isActive = true
  )
}

fun GoalEntity.toGoalItem(): GoalItem {
  return GoalItem(
    id = id,
    title = title,
    description = description,
    goalType = goalType,
    category = category,
    currentProgress = currentProgress,
    targetProgress = targetProgress,
    unit = unit,
    deadline = deadline,
    isCompleted = isCompleted,
    createdAt = createdAt
  )
}

fun GoalItem.toEntity(): GoalEntity {
  return GoalEntity(
    id = id,
    title = title,
    description = description,
    goalType = goalType,
    category = category,
    currentProgress = currentProgress,
    targetProgress = targetProgress,
    unit = unit,
    deadline = deadline,
    isCompleted = isCompleted,
    createdAt = createdAt
  )
}
