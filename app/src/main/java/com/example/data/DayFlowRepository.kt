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
import com.example.model.CustomCategory
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
        title = "Learn Spanish Fluently",
        description = "Practice daily vocabulary, grammar and speaking exercises",
        goalType = "LONG TERM",
        category = ItemCategory.LEARNING,
        currentProgress = 35,
        targetProgress = 100,
        unit = "%",
        deadline = "180d left",
        isCompleted = false
      ),
      GoalItem(
        id = "g2",
        title = "Launch Portfolio Website",
        description = "Design and build personal developer showcase site",
        goalType = "SHORT TERM",
        category = ItemCategory.WORK,
        currentProgress = 80,
        targetProgress = 100,
        unit = "%",
        deadline = "14d left",
        isCompleted = false
      ),
      GoalItem(
        id = "g3",
        title = "Read 12 Books",
        description = "Non-fiction, biographies, and technical literature",
        goalType = "SHORT TERM",
        category = ItemCategory.LEARNING,
        currentProgress = 100,
        targetProgress = 100,
        unit = "%",
        deadline = "Dec 2023",
        isCompleted = true
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

  // Custom Categories
  val customCategories: Flow<List<CustomCategory>> = preferencesManager?.customCategories ?: flowOf(emptyList())

  fun saveCustomCategory(category: CustomCategory) {
    preferencesManager?.saveCustomCategory(category)
  }

  fun deleteCustomCategory(categoryId: String) {
    preferencesManager?.deleteCustomCategory(categoryId)
  }

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
        val newProgress = (goal.currentProgress + increment).coerceIn(0, 100)
        val isCompleted = newProgress >= 100
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
            val newProgress = (goal.currentProgress + increment).coerceIn(0, 100)
            goal.copy(
              currentProgress = newProgress,
              isCompleted = newProgress >= 100
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
      val validProgress = newProgress.coerceIn(0, 100)
      val isCompleted = validProgress >= 100
      if (goalDao != null && goalProgressDao != null) {
        val goal = goalDao.getGoalById(goalId) ?: return@launch
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
            goal.copy(currentProgress = validProgress, isCompleted = isCompleted)
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
        val nextProgress = if (nextCompleted) 100 else (if (goal.currentProgress >= 100) 0 else goal.currentProgress)
        goalDao.updateGoalProgress(goalId, nextProgress, nextCompleted)
      } else {
        _inMemoryGoals.value = _inMemoryGoals.value.map { goal ->
          if (goal.id == goalId) {
            val nextCompleted = !goal.isCompleted
            val nextProgress = if (nextCompleted) 100 else (if (goal.currentProgress >= 100) 0 else goal.currentProgress)
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
      val isCompleted = goal.currentProgress >= 100
      val updatedGoal = goal.copy(isCompleted = isCompleted)
      if (goalDao != null) {
        goalDao.updateGoal(updatedGoal.toEntity())
      } else {
        _inMemoryGoals.value = _inMemoryGoals.value.map { if (it.id == updatedGoal.id) updatedGoal else it }
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
    
    // Dynamic streak calculation
    val todayKey = DateUtils.getTodayDateKey()
    val completedDates = mutableSetOf<String>()
    taskList.filter { it.isCompleted }.forEach {
      val d = if (it.dueDate == "Today" || it.dueDate.isBlank()) todayKey else it.dueDate
      completedDates.add(d)
    }
    if (habitList.any { it.completedToday }) {
      completedDates.add(todayKey)
    }
    val (calculatedCurrentStreak, _) = DateUtils.calculateStreak(completedDates, todayKey)
    val maxHabitStreak = habitList.maxOfOrNull { it.streakDays } ?: 0
    val finalStreak = maxOf(calculatedCurrentStreak, maxHabitStreak)

    return DailyProgressSummary(
      totalTasks = taskList.size,
      completedTasks = completedCount,
      habitsCompleted = completedHabits,
      totalHabits = habitList.size,
      focusMinutes = focusMins,
      currentStreak = finalStreak
    )
  }

  fun calculateStatistics(
    timeRange: StatsTimeRange,
    taskList: List<TaskItem>,
    habitList: List<HabitItem>,
    goalList: List<GoalItem> = emptyList()
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

    // Deep Focus calculation
    val completedFocusTasks = inRangeTasks.filter { it.isCompleted && it.estimatedMinutes > 0 }
    val totalFocusMinutes = completedFocusTasks.sumOf { it.estimatedMinutes }
    val plannedFocusMinutes = inRangeTasks.sumOf { it.estimatedMinutes }
    val focusSessionsCount = completedFocusTasks.size
    val longestFocusMinutes = completedFocusTasks.maxOfOrNull { it.estimatedMinutes } ?: 0
    val avgFocusMinutes = if (focusSessionsCount > 0) totalFocusMinutes / focusSessionsCount else 0

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

    // Comprehensive Streak calculation (across all task completions and habit activities)
    val completedDates = tasksByDate.filter { (_, list) -> list.any { it.isCompleted } }.keys.toMutableSet()
    if (habitList.any { it.completedToday }) {
      completedDates.add(todayKey)
    }

    val (calculatedCurrentStreak, calculatedBestStreak) = DateUtils.calculateStreak(completedDates, todayKey)
    val habitMaxStreak = habitList.maxOfOrNull { it.streakDays } ?: 0
    val finalCurrentStreak = maxOf(calculatedCurrentStreak, if (habitList.any { it.completedToday }) habitMaxStreak else 0)
    val finalBestStreak = maxOf(finalCurrentStreak, calculatedBestStreak, habitMaxStreak)

    // Recent 5 days completion dots
    val recent5Keys = DateUtils.getLastNDaysKeys(5)
    val recentStreakDays = recent5Keys.map { completedDates.contains(it) }

    // Category breakdown across in-range tasks and all active tasks/habits
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

    val hasAnyActivity = tasksPlanned > 0 || tasksCompleted > 0 || taskList.isNotEmpty() || habitList.isNotEmpty() || goalList.isNotEmpty()

    return StatisticsData(
      timeRange = timeRange,
      tasksCompleted = tasksCompleted,
      tasksPlanned = tasksPlanned,
      completionRate = completionRate,
      currentStreak = finalCurrentStreak,
      bestStreak = finalBestStreak,
      totalFocusMinutes = totalFocusMinutes,
      plannedFocusMinutes = plannedFocusMinutes,
      focusSessionsCount = focusSessionsCount,
      avgFocusMinutes = avgFocusMinutes,
      longestFocusMinutes = longestFocusMinutes,
      dailyStats = dailyStats,
      categoryStats = categoryStats,
      recentStreakDays = recentStreakDays,
      hasAnyActivity = hasAnyActivity
    )
  }

  fun manualAddStreakDay() {
    coroutineScope.launch {
      val todayKey = DateUtils.getTodayDateKey()
      val allCompletions = if (habitCompletionDao != null) habitCompletionDao.getAllCompletionsList() else emptyList()
      val allTasks = if (taskDao != null) taskDao.getAllTasksList() else _inMemoryTasks.value.map { it.toEntity() }
      val completedDates = mutableSetOf<String>()
      allCompletions.filter { it.isCompleted }.forEach { completedDates.add(it.completionDate) }
      allTasks.filter { it.isCompleted }.forEach {
        val d = if (it.dueDate == "Today" || it.dueDate.isBlank()) todayKey else it.dueDate
        completedDates.add(d)
      }

      val (currentStreak, _) = DateUtils.calculateStreak(completedDates, todayKey)
      val targetDateKey = if (!completedDates.contains(todayKey)) {
        todayKey
      } else {
        DateUtils.getDateKeyOffset(todayKey, -currentStreak)
      }

      if (habitCompletionDao != null) {
        habitCompletionDao.insertCompletion(
          HabitCompletionEntity(
            id = "streak_manual_${UUID.randomUUID()}",
            habitId = "streak_tracker",
            completionDate = targetDateKey,
            progressValue = 1,
            isCompleted = true
          )
        )
      } else {
        _inMemoryHabits.value = _inMemoryHabits.value.map {
          it.copy(streakDays = it.streakDays + 1)
        }
      }
    }
  }

  fun manualRemoveStreakDay() {
    coroutineScope.launch {
      val todayKey = DateUtils.getTodayDateKey()
      val allCompletions = if (habitCompletionDao != null) habitCompletionDao.getAllCompletionsList() else emptyList()
      val allTasks = if (taskDao != null) taskDao.getAllTasksList() else _inMemoryTasks.value.map { it.toEntity() }
      val completedDates = mutableSetOf<String>()
      allCompletions.filter { it.isCompleted }.forEach { completedDates.add(it.completionDate) }
      allTasks.filter { it.isCompleted }.forEach {
        val d = if (it.dueDate == "Today" || it.dueDate.isBlank()) todayKey else it.dueDate
        completedDates.add(d)
      }

      val (currentStreak, _) = DateUtils.calculateStreak(completedDates, todayKey)
      if (currentStreak <= 0) return@launch

      val targetDateKey = if (completedDates.contains(todayKey)) {
        todayKey
      } else {
        DateUtils.getDateKeyOffset(todayKey, -1)
      }

      if (habitCompletionDao != null) {
        val habitList = habitDao?.getAllHabitsList() ?: emptyList()
        for (h in habitList) {
          habitCompletionDao.deleteCompletion(h.id, targetDateKey)
        }
        habitCompletionDao.deleteCompletion("streak_tracker", targetDateKey)
      }
      if (taskDao != null) {
        val dayTasks = taskDao.getTasksByDateSync(targetDateKey)
        for (t in dayTasks) {
          if (t.isCompleted) {
            taskDao.updateTaskCompletion(t.id, false)
          }
        }
      }
      _inMemoryHabits.value = _inMemoryHabits.value.map {
        it.copy(streakDays = (it.streakDays - 1).coerceAtLeast(0))
      }
    }
  }

  fun manualResetStreak() {
    coroutineScope.launch {
      val todayKey = DateUtils.getTodayDateKey()
      val allCompletions = if (habitCompletionDao != null) habitCompletionDao.getAllCompletionsList() else emptyList()
      val allTasks = if (taskDao != null) taskDao.getAllTasksList() else _inMemoryTasks.value.map { it.toEntity() }
      val completedDates = mutableSetOf<String>()
      allCompletions.filter { it.isCompleted }.forEach { completedDates.add(it.completionDate) }
      allTasks.filter { it.isCompleted }.forEach {
        val d = if (it.dueDate == "Today" || it.dueDate.isBlank()) todayKey else it.dueDate
        completedDates.add(d)
      }

      val (currentStreak, _) = DateUtils.calculateStreak(completedDates, todayKey)
      val habitList = habitDao?.getAllHabitsList() ?: emptyList()

      for (i in 0..(currentStreak + 1)) {
        val dateKey = DateUtils.getDateKeyOffset(todayKey, -i)
        if (habitCompletionDao != null) {
          for (h in habitList) {
            habitCompletionDao.deleteCompletion(h.id, dateKey)
          }
          habitCompletionDao.deleteCompletion("streak_tracker", dateKey)
        }
        if (taskDao != null) {
          val dayTasks = taskDao.getTasksByDateSync(dateKey)
          for (t in dayTasks) {
            if (t.isCompleted) {
              taskDao.updateTaskCompletion(t.id, false)
            }
          }
        }
      }
      if (habitDao != null) {
        for (h in habitList) {
          habitDao.updateHabitStreak(h.id, 0)
        }
      }
      _inMemoryHabits.value = _inMemoryHabits.value.map {
        it.copy(streakDays = 0, completedToday = false)
      }
    }
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
    endTime = endTime,
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
    endTime = endTime,
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
