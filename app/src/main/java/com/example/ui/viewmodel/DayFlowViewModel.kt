package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.DayFlowRepository
import com.example.data.local.DayFlowDatabase
import com.example.model.CalendarEventItem
import com.example.model.CoachInsight
import com.example.model.CustomCategory
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class DayFlowViewModel(
  private val repository: DayFlowRepository = DayFlowRepository()
) : ViewModel() {

  companion object {
    fun provideFactory(context: android.content.Context): ViewModelProvider.Factory =
      object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          val database = DayFlowDatabase.getDatabase(context)
          val preferencesManager = com.example.data.local.UserPreferencesManager(context.applicationContext)
          val repository = DayFlowRepository.fromDatabase(database, preferencesManager)
          return DayFlowViewModel(repository) as T
        }
      }
  }

  // Selected date for Today Page (defaults to real current day ISO string)
  private val _selectedTodayDate = MutableStateFlow(DateUtils.getTodayDateKey())
  val selectedTodayDate: StateFlow<String> = _selectedTodayDate.asStateFlow()

  val allTasks: StateFlow<List<TaskItem>> = repository.tasks
  val allHabits: StateFlow<List<HabitItem>> = repository.habits
  val goals: StateFlow<List<GoalItem>> = repository.goals
  val calendarEvents: StateFlow<List<CalendarEventItem>> = repository.calendarEvents
  val coachInsights: StateFlow<List<CoachInsight>> = repository.coachInsights

  // Tasks for the selected date on Today page
  val todayTasks: StateFlow<List<TaskItem>> = _selectedTodayDate.flatMapLatest { date ->
    repository.getTasksForDate(date)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  // Habits with date-specific progress for the selected date
  val todayHabits: StateFlow<List<HabitItem>> = _selectedTodayDate.flatMapLatest { date ->
    repository.getHabitsForDate(date)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  private val _selectedCategory = MutableStateFlow<ItemCategory?>(null)
  val selectedCategory: StateFlow<ItemCategory?> = _selectedCategory.asStateFlow()

  private val _isAddTaskSheetOpen = MutableStateFlow(false)
  val isAddTaskSheetOpen: StateFlow<Boolean> = _isAddTaskSheetOpen.asStateFlow()

  private val _editingTask = MutableStateFlow<TaskItem?>(null)
  val editingTask: StateFlow<TaskItem?> = _editingTask.asStateFlow()

  private val _isAddHabitSheetOpen = MutableStateFlow(false)
  val isAddHabitSheetOpen: StateFlow<Boolean> = _isAddHabitSheetOpen.asStateFlow()

  private val _habitForProgressSheet = MutableStateFlow<HabitItem?>(null)
  val habitForProgressSheet: StateFlow<HabitItem?> = _habitForProgressSheet.asStateFlow()

  // Calendar Screen state
  private val _selectedCalendarDate = MutableStateFlow(DateUtils.getTodayDateKey())
  val selectedCalendarDate: StateFlow<String> = _selectedCalendarDate.asStateFlow()

  private val _calendarYear = MutableStateFlow(DateUtils.getCurrentYear())
  val calendarYear: StateFlow<Int> = _calendarYear.asStateFlow()

  private val _calendarMonth = MutableStateFlow(DateUtils.getCurrentMonth())
  val calendarMonth: StateFlow<Int> = _calendarMonth.asStateFlow()

  // Tasks for the selected date on Calendar page
  val calendarTasks: StateFlow<List<TaskItem>> = _selectedCalendarDate.flatMapLatest { date ->
    repository.getTasksForDate(date)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  // Target date when opening Add Task sheet
  private val _activeTaskTargetDate = MutableStateFlow<String?>(null)
  val activeTaskTargetDate: StateFlow<String?> = _activeTaskTargetDate.asStateFlow()

  // Dynamic progress summary calculated strictly for the currently selected date
  val progressSummary: StateFlow<DailyProgressSummary> = combine(todayTasks, todayHabits) { taskList, habitList ->
    val completedTasks = taskList.count { it.isCompleted }
    val completedHabits = habitList.count { it.completedToday }
    val focusMins = taskList.filter { it.isCompleted }.sumOf { it.estimatedMinutes }
    val maxStreak = habitList.maxOfOrNull { it.streakDays } ?: 0

    DailyProgressSummary(
      totalTasks = taskList.size,
      completedTasks = completedTasks,
      habitsCompleted = completedHabits,
      totalHabits = habitList.size,
      focusMinutes = focusMins,
      currentStreak = maxStreak
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = DailyProgressSummary(0, 0, 0, 0, 0, 0)
  )

  // Statistics Screen State
  private val _statsTimeRange = MutableStateFlow(StatsTimeRange.DAYS_7)
  val statsTimeRange: StateFlow<StatsTimeRange> = _statsTimeRange.asStateFlow()

  val customCategories: StateFlow<List<CustomCategory>> = repository.customCategories.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  val statisticsData: StateFlow<StatisticsData> = combine(
    _statsTimeRange,
    allTasks,
    allHabits,
    goals
  ) { range: StatsTimeRange, taskList: List<TaskItem>, habitList: List<HabitItem>, goalList: List<GoalItem> ->
    repository.calculateStatistics(range, taskList, habitList, goalList)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = StatisticsData()
  )

  fun setStatsTimeRange(range: StatsTimeRange) {
    _statsTimeRange.value = range
  }

  fun addStreakDay() {
    repository.manualAddStreakDay()
  }

  fun removeStreakDay() {
    repository.manualRemoveStreakDay()
  }

  fun resetStreak() {
    repository.manualResetStreak()
  }

  fun selectTodayDate(dateKey: String) {
    _selectedTodayDate.value = dateKey
  }

  fun toggleTask(taskId: String) {
    repository.toggleTask(taskId)
  }

  fun addTask(
    title: String,
    description: String,
    category: ItemCategory,
    priority: TaskPriority,
    time: String,
    estimatedMinutes: Int,
    endTime: String? = null
  ) {
    if (title.isBlank()) return
    val targetDate = _activeTaskTargetDate.value ?: _selectedTodayDate.value
    val newTask = TaskItem(
      id = UUID.randomUUID().toString(),
      title = title.trim(),
      description = description.trim(),
      category = category,
      priority = priority,
      time = if (time.isBlank()) "09:00 AM" else time,
      endTime = endTime,
      dueDate = targetDate,
      isCompleted = false,
      estimatedMinutes = estimatedMinutes
    )
    repository.addTask(newTask)
    _isAddTaskSheetOpen.value = false
    _activeTaskTargetDate.value = null
  }

  fun updateTask(task: TaskItem) {
    repository.updateTask(task)
    _editingTask.value = null
  }

  fun deleteTask(taskId: String) {
    repository.deleteTask(taskId)
    _editingTask.value = null
  }

  fun openEditTaskSheet(task: TaskItem) {
    _editingTask.value = task
  }

  fun closeEditTaskSheet() {
    _editingTask.value = null
  }

  fun toggleHabit(habitId: String) {
    repository.toggleHabit(habitId, _selectedTodayDate.value)
  }

  fun updateHabitProgress(habitId: String, newProgress: Int) {
    repository.updateHabitProgress(habitId, _selectedTodayDate.value, newProgress)
  }

  fun addHabit(
    title: String,
    category: ItemCategory,
    dailyTarget: Int,
    unit: String,
    reminderTime: String
  ) {
    if (title.isBlank()) return
    val newHabit = HabitItem(
      id = UUID.randomUUID().toString(),
      title = title.trim(),
      category = category,
      streakDays = 0,
      targetPerWeek = 7,
      dailyTarget = dailyTarget.coerceAtLeast(1),
      unit = unit.trim(),
      currentProgress = 0,
      completedToday = false,
      reminderTime = reminderTime
    )
    repository.addHabit(newHabit)
    _isAddHabitSheetOpen.value = false
  }

  fun deleteHabit(habitId: String) {
    repository.deleteHabit(habitId)
    _habitForProgressSheet.value = null
  }

  fun saveCustomCategory(category: CustomCategory) {
    repository.saveCustomCategory(category)
  }

  fun deleteCustomCategory(categoryId: String) {
    repository.deleteCustomCategory(categoryId)
  }

  fun openAddHabitSheet() {
    _isAddHabitSheetOpen.value = true
  }

  fun closeAddHabitSheet() {
    _isAddHabitSheetOpen.value = false
  }

  fun openHabitProgressSheet(habit: HabitItem) {
    _habitForProgressSheet.value = habit
  }

  fun closeHabitProgressSheet() {
    _habitForProgressSheet.value = null
  }

  fun updateGoalProgress(goalId: String, increment: Int) {
    repository.updateGoalProgress(goalId, increment)
  }

  fun setGoalProgress(goalId: String, newProgress: Int) {
    repository.setGoalProgress(goalId, newProgress)
  }

  fun toggleGoalCompletion(goalId: String) {
    repository.toggleGoalCompletion(goalId)
  }

  fun updateGoal(goal: GoalItem) {
    repository.updateGoal(goal)
  }

  fun deleteGoal(goalId: String) {
    repository.deleteGoal(goalId)
  }

  fun addGoal(
    title: String,
    description: String = "",
    goalType: String = "LONG TERM",
    category: ItemCategory = ItemCategory.LEARNING,
    target: Int = 100,
    unit: String = "%",
    deadline: String = "180d left",
    initialProgress: Int = 0
  ) {
    if (title.isBlank()) return
    val newGoal = GoalItem(
      id = UUID.randomUUID().toString(),
      title = title.trim(),
      description = description.trim(),
      goalType = if (goalType.isNotBlank()) goalType else "LONG TERM",
      category = category,
      currentProgress = initialProgress.coerceIn(0, target.coerceAtLeast(1)),
      targetProgress = target.coerceAtLeast(1),
      unit = if (unit.isBlank()) "%" else unit.trim(),
      deadline = if (deadline.isBlank()) "180d left" else deadline.trim(),
      isCompleted = initialProgress >= target.coerceAtLeast(1) && initialProgress > 0,
      createdAt = System.currentTimeMillis()
    )
    repository.addGoal(newGoal)
  }

  fun setCategoryFilter(category: ItemCategory?) {
    _selectedCategory.value = category
  }

  fun setSelectedCalendarDate(date: String) {
    _selectedCalendarDate.value = date
  }

  fun nextCalendarMonth() {
    val currentMonth = _calendarMonth.value
    val currentYear = _calendarYear.value
    if (currentMonth == 11) {
      _calendarMonth.value = 0
      _calendarYear.value = currentYear + 1
    } else {
      _calendarMonth.value = currentMonth + 1
    }
  }

  fun prevCalendarMonth() {
    val currentMonth = _calendarMonth.value
    val currentYear = _calendarYear.value
    if (currentMonth == 0) {
      _calendarMonth.value = 11
      _calendarYear.value = currentYear - 1
    } else {
      _calendarMonth.value = currentMonth - 1
    }
  }

  fun openAddTaskSheet(targetDate: String? = null) {
    _activeTaskTargetDate.value = targetDate
    _isAddTaskSheetOpen.value = true
  }

  fun closeAddTaskSheet() {
    _isAddTaskSheetOpen.value = false
    _activeTaskTargetDate.value = null
  }

  // Preferences
  private val preferencesManager = repository.getPreferencesManager()

  val themeMode: StateFlow<com.example.data.local.AppThemeMode> = preferencesManager?.themeMode
    ?: MutableStateFlow(com.example.data.local.AppThemeMode.SYSTEM).asStateFlow()

  val accentColor: StateFlow<com.example.ui.theme.DayFlowAccent> = preferencesManager?.accentColor
    ?: MutableStateFlow(com.example.ui.theme.DayFlowAccent.ROSEWOOD).asStateFlow()

  val notifications: StateFlow<com.example.data.local.NotificationPreferences> = preferencesManager?.notifications
    ?: MutableStateFlow(com.example.data.local.NotificationPreferences()).asStateFlow()

  fun setThemeMode(mode: com.example.data.local.AppThemeMode) {
    preferencesManager?.setThemeMode(mode)
  }

  fun setAccentColor(accent: com.example.ui.theme.DayFlowAccent) {
    preferencesManager?.setAccentColor(accent)
  }

  fun setNotificationsEnabled(enabled: Boolean) {
    preferencesManager?.setNotificationsEnabled(enabled)
  }

  fun setMorningBriefing(enabled: Boolean) {
    preferencesManager?.setMorningBriefing(enabled)
  }

  fun setEveningReview(enabled: Boolean) {
    preferencesManager?.setEveningReview(enabled)
  }

  fun setHabitReminders(enabled: Boolean) {
    preferencesManager?.setHabitReminders(enabled)
  }

  // Backup & Restore
  suspend fun exportBackupJson(): String {
    return repository.exportBackupJson()
  }

  suspend fun importBackupJson(jsonString: String): Result<com.example.data.local.ImportResultSummary> {
    return repository.importBackupJson(jsonString)
  }

  // AI Coach Chat
  private val _aiChatMessages = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList())
  val aiChatMessages: StateFlow<List<Pair<String, Boolean>>> = _aiChatMessages.asStateFlow()

  private val _isAiThinking = MutableStateFlow(false)
  val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

  fun sendCoachPrompt(prompt: String) {
    if (prompt.isBlank()) return
    val userText = prompt.trim()
    _aiChatMessages.value = _aiChatMessages.value + (userText to true)

    viewModelScope.launch {
      _isAiThinking.value = true
      val context = com.example.data.ai.AiCoachContext(
        todayTasks = todayTasks.value,
        todayHabits = todayHabits.value,
        activeGoals = goals.value,
        summary = progressSummary.value,
        selectedDate = _selectedTodayDate.value
      )

      val response = com.example.data.ai.DayFlowAiService.generateCoachResponse(
        actionType = com.example.data.ai.CoachActionType.ASK_AI,
        userQuery = userText,
        context = context
      )

      _aiChatMessages.value = _aiChatMessages.value + (response to false)
      _isAiThinking.value = false

      val newInsight = CoachInsight(
        id = UUID.randomUUID().toString(),
        title = "AI Reflection & Advice",
        description = response,
        type = InsightType.ADVICE,
        timestamp = "Just now"
      )
      repository.addCoachInsight(newInsight)
    }
  }

  fun triggerCoachAction(actionType: com.example.data.ai.CoachActionType) {
    val actionLabel = when (actionType) {
      com.example.data.ai.CoachActionType.DAILY_BRIEFING -> "Give me a daily briefing"
      com.example.data.ai.CoachActionType.DAY_REVIEW -> "Review my progress today"
      com.example.data.ai.CoachActionType.GOAL_GUIDANCE -> "Provide guidance on my active goals"
      com.example.data.ai.CoachActionType.ASK_AI -> "Summary analysis"
    }

    _aiChatMessages.value = _aiChatMessages.value + (actionLabel to true)

    viewModelScope.launch {
      _isAiThinking.value = true
      val context = com.example.data.ai.AiCoachContext(
        todayTasks = todayTasks.value,
        todayHabits = todayHabits.value,
        activeGoals = goals.value,
        summary = progressSummary.value,
        selectedDate = _selectedTodayDate.value
      )

      val response = com.example.data.ai.DayFlowAiService.generateCoachResponse(
        actionType = actionType,
        userQuery = "",
        context = context
      )

      _aiChatMessages.value = _aiChatMessages.value + (response to false)
      _isAiThinking.value = false
    }
  }
}
