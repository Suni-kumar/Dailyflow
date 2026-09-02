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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
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

  private val _editingHabit = MutableStateFlow<HabitItem?>(null)
  val editingHabit: StateFlow<HabitItem?> = _editingHabit.asStateFlow()

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
  val progressSummary: StateFlow<DailyProgressSummary> = combine(
    todayTasks,
    todayHabits,
    repository.manualStreakOffset
  ) { taskList, habitList, streakOffset ->
    val completedTasks = taskList.count { it.isCompleted }
    val completedHabits = habitList.count { it.completedToday }
    val focusMins = taskList.filter { it.isCompleted }.sumOf { it.estimatedMinutes }
    val maxStreak = habitList.maxOfOrNull { it.streakDays } ?: 0
    val effectiveStreak = (maxStreak + streakOffset).coerceAtLeast(0)

    DailyProgressSummary(
      totalTasks = taskList.size,
      completedTasks = completedTasks,
      habitsCompleted = completedHabits,
      totalHabits = habitList.size,
      focusMinutes = focusMins,
      currentStreak = effectiveStreak
    )
  }.flowOn(Dispatchers.Default)
  .stateIn(
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
    goals,
    repository.manualStreakOffset
  ) { range: StatsTimeRange, taskList: List<TaskItem>, habitList: List<HabitItem>, goalList: List<GoalItem>, streakOffset: Int ->
    repository.calculateStatistics(range, taskList, habitList, goalList, streakOffset)
  }.flowOn(Dispatchers.Default)
  .stateIn(
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

  fun openEditHabitSheet(habit: HabitItem) {
    _editingHabit.value = habit
    _habitForProgressSheet.value = null
  }

  fun closeEditHabitSheet() {
    _editingHabit.value = null
  }

  fun updateHabit(habit: HabitItem) {
    repository.updateHabit(habit)
    _editingHabit.value = null
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

  val geminiApiKey: StateFlow<String> = preferencesManager?.geminiApiKey
    ?: MutableStateFlow("").asStateFlow()

  val geminiConnectionVerified: StateFlow<Boolean> = preferencesManager?.geminiConnectionVerified
    ?: MutableStateFlow(false).asStateFlow()

  val aiLanguage: StateFlow<com.example.data.local.AiLanguage> = preferencesManager?.aiLanguage
    ?: MutableStateFlow(com.example.data.local.AiLanguage.AUTO).asStateFlow()

  private val _testConnectionResult = MutableStateFlow<com.example.data.ai.ConnectionTestResult?>(null)
  val testConnectionResult: StateFlow<com.example.data.ai.ConnectionTestResult?> = _testConnectionResult.asStateFlow()

  private val _isTestingConnection = MutableStateFlow(false)
  val isTestingConnection: StateFlow<Boolean> = _isTestingConnection.asStateFlow()

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

  fun setGeminiApiKey(key: String) {
    preferencesManager?.setGeminiApiKey(key)
    _testConnectionResult.value = null
  }

  fun clearGeminiApiKey() {
    preferencesManager?.clearGeminiApiKey()
    _testConnectionResult.value = null
  }

  fun setAiLanguage(language: com.example.data.local.AiLanguage) {
    preferencesManager?.setAiLanguage(language)
  }

  fun testGeminiConnection(keyToTest: String? = null) {
    val key = keyToTest ?: geminiApiKey.value
    viewModelScope.launch {
      _isTestingConnection.value = true
      _testConnectionResult.value = null
      val result = com.example.data.ai.DayFlowAiService.testConnection(key)
      _testConnectionResult.value = result
      _isTestingConnection.value = false
      if (result is com.example.data.ai.ConnectionTestResult.Success) {
        preferencesManager?.setGeminiConnectionVerified(true)
      } else {
        preferencesManager?.setGeminiConnectionVerified(false)
      }
    }
  }

  fun clearTestConnectionResult() {
    _testConnectionResult.value = null
  }

  // Backup & Restore
  suspend fun exportBackupJson(): String {
    return repository.exportBackupJson()
  }

  suspend fun importBackupJson(jsonString: String): Result<com.example.data.local.ImportResultSummary> {
    return repository.importBackupJson(jsonString)
  }

  // AI Coach Chat (Phase 1 Multi-turn with Real Streaming)
  private val _aiChatMessages = MutableStateFlow<List<com.example.model.AiChatMessage>>(emptyList())
  val aiChatMessages: StateFlow<List<com.example.model.AiChatMessage>> = _aiChatMessages.asStateFlow()

  val aiChatSessions: StateFlow<List<com.example.model.AiChatSession>> = repository.getAllChatSessions()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val aiMemories: StateFlow<List<com.example.model.AiMemory>> = repository.getAllMemories()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _currentSessionId = MutableStateFlow<String?>(null)
  val currentSessionId: StateFlow<String?> = _currentSessionId.asStateFlow()

  private val _isAiThinking = MutableStateFlow(false)
  val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

  private var activeAiJob: kotlinx.coroutines.Job? = null

  fun sendCoachPrompt(prompt: String) {
    if (prompt.isBlank() || _isAiThinking.value) return
    val userText = prompt.trim()
    
    // Simple local explicit memory detection
    val lowerText = userText.lowercase()
    if (lowerText.startsWith("remember that ") || lowerText.startsWith("remember to ") || lowerText.startsWith("please remember ")) {
      val memoryText = userText.replace(Regex("^(please )?remember (that|to) ", RegexOption.IGNORE_CASE), "").trim()
      if (memoryText.isNotBlank()) {
        explicitRemember(memoryText)
      }
    }

    val userMsg = com.example.model.AiChatMessage(
      text = userText,
      isUser = true,
      timestamp = "Just now"
    )

    viewModelScope.launch {
      var sessionId = _currentSessionId.value
      if (sessionId == null) {
        sessionId = UUID.randomUUID().toString()
        _currentSessionId.value = sessionId
        val title = if (userText.length > 25) userText.take(25) + "..." else userText
        val newSession = com.example.model.AiChatSession(
          id = sessionId,
          title = title,
          createdAt = System.currentTimeMillis(),
          updatedAt = System.currentTimeMillis()
        )
        repository.insertOrUpdateSession(newSession)
      } else {
        repository.updateSessionTimestamp(sessionId)
      }
      repository.insertMessage(sessionId, userMsg)
    }

    val currentHistory = _aiChatMessages.value + userMsg
    val assistantMsgId = UUID.randomUUID().toString()
    val assistantMsgPlaceholder = com.example.model.AiChatMessage(
      id = assistantMsgId,
      text = "",
      isUser = false,
      timestamp = "Just now",
      isStreaming = true
    )

    _aiChatMessages.value = currentHistory + assistantMsgPlaceholder
    _isAiThinking.value = true

    executeStreamingPrompt(
      history = currentHistory,
      assistantMsgId = assistantMsgId
    )
  }

  fun triggerCoachAction(actionType: com.example.data.ai.CoachActionType) {
    if (_isAiThinking.value) return
    val actionPrompt = when (actionType) {
      com.example.data.ai.CoachActionType.DAILY_BRIEFING -> "Please give me a calm and actionable daily briefing analyzing today's scheduled tasks, active habits, and priority focus areas."
      com.example.data.ai.CoachActionType.DAY_REVIEW -> "Let's review my progress today: what tasks were completed, what remains unfinished, habit consistency, and mindful reflections for tomorrow."
      com.example.data.ai.CoachActionType.GOAL_GUIDANCE -> "Analyze my active goals, current progress, deadlines, and suggest practical next actions to move them forward."
      com.example.data.ai.CoachActionType.ASK_AI -> "How is my day looking so far?"
    }

    sendCoachPrompt(actionPrompt)
  }

  private fun executeStreamingPrompt(
    history: List<com.example.model.AiChatMessage>,
    assistantMsgId: String
  ) {
    activeAiJob?.cancel()
    activeAiJob = viewModelScope.launch {
      val latestUserPrompt = history.lastOrNull { it.isUser }?.text.orEmpty()
      val context = com.example.data.ai.AiCoachContext(
        todayTasks = todayTasks.value,
        todayHabits = todayHabits.value,
        activeGoals = goals.value,
        summary = progressSummary.value,
        selectedDate = _selectedTodayDate.value,
        statisticsData = statisticsData.value,
        currentDateTimeString = com.example.util.DateUtils.getFullCurrentDateTimeString(),
        queryIntent = com.example.data.ai.DayFlowContextBuilder.detectIntent(latestUserPrompt),
        memories = aiMemories.value
      )

      val accumulatedResponse = StringBuilder()

      try {
        val streamResult = com.example.data.ai.DayFlowAiService.streamCoachResponse(
          conversationHistory = history,
          context = context,
          language = aiLanguage.value,
          userApiKey = geminiApiKey.value,
          onChunk = { chunk ->
            accumulatedResponse.append(chunk)
            updateAssistantMessage(assistantMsgId, accumulatedResponse.toString(), isStreaming = true)
          }
        )

        val finalText = if (accumulatedResponse.isNotEmpty()) accumulatedResponse.toString() else streamResult.text
        updateAssistantMessage(
          assistantMsgId = assistantMsgId,
          text = finalText,
          isStreaming = false
        )

        if (streamResult.mode == com.example.data.ai.AiResponseMode.LIVE_GEMINI) {
          if (geminiApiKey.value.isNotBlank()) {
            preferencesManager?.setGeminiConnectionVerified(true)
          }
        } else if (streamResult.isErrorFallback) {
          if (streamResult.errorMessage?.contains("401") == true ||
            streamResult.errorMessage?.contains("403") == true ||
            streamResult.errorMessage?.contains("404") == true
          ) {
            preferencesManager?.setGeminiConnectionVerified(false)
          }
        }

        // Save final message to DB
        _currentSessionId.value?.let { sid ->
          val msgToSave = _aiChatMessages.value.find { it.id == assistantMsgId }
          if (msgToSave != null && !msgToSave.isError) {
            repository.insertMessage(sid, msgToSave)
            repository.updateSessionTimestamp(sid)
          }
        }

        // Add dynamic insight for major reflections
        val insightTitle = if (history.lastOrNull()?.text?.contains("briefing", ignoreCase = true) == true) {
          "Morning Briefing Reflection"
        } else if (history.lastOrNull()?.text?.contains("review", ignoreCase = true) == true) {
          "Evening Review Reflection"
        } else {
          "AI Mindful Coaching"
        }

        val newInsight = CoachInsight(
          id = UUID.randomUUID().toString(),
          title = insightTitle,
          description = streamResult.text.take(180),
          type = InsightType.ADVICE,
          timestamp = "Today"
        )
        repository.addCoachInsight(newInsight)
      } catch (e: kotlinx.coroutines.CancellationException) {
        // User stopped generation
        val currentText = accumulatedResponse.toString()
        if (currentText.isNotBlank()) {
          updateAssistantMessage(assistantMsgId, currentText, isStreaming = false)
          _currentSessionId.value?.let { sid ->
            val msgToSave = _aiChatMessages.value.find { it.id == assistantMsgId }
            if (msgToSave != null) repository.insertMessage(sid, msgToSave)
          }
        } else {
          _aiChatMessages.value = _aiChatMessages.value.filter { it.id != assistantMsgId }
        }
      } catch (e: Exception) {
        updateAssistantMessage(
          assistantMsgId = assistantMsgId,
          text = accumulatedResponse.toString(),
          isStreaming = false,
          isError = true,
          errorMessage = e.message ?: "Failed to generate response."
        )
      } finally {
        _isAiThinking.value = false
      }
    }
  }

  private fun updateAssistantMessage(
    assistantMsgId: String,
    text: String,
    isStreaming: Boolean,
    isError: Boolean = false,
    errorMessage: String? = null
  ) {
    _aiChatMessages.value = _aiChatMessages.value.map { msg ->
      if (msg.id == assistantMsgId) {
        msg.copy(
          text = text,
          isStreaming = isStreaming,
          isError = isError,
          errorMessage = errorMessage
        )
      } else {
        msg
      }
    }
  }

  fun stopAiGeneration() {
    activeAiJob?.cancel()
    activeAiJob = null
    _isAiThinking.value = false
    _aiChatMessages.value = _aiChatMessages.value.map {
      if (it.isStreaming) it.copy(isStreaming = false) else it
    }
  }

  fun retryLastAiMessage() {
    val messages = _aiChatMessages.value
    if (messages.isEmpty()) return
    val lastUserMsgIndex = messages.indexOfLast { it.isUser }
    if (lastUserMsgIndex >= 0) {
      val userPrompt = messages[lastUserMsgIndex].text
      // Remove failed assistant turn and subsequent messages
      _aiChatMessages.value = messages.take(lastUserMsgIndex)
      sendCoachPrompt(userPrompt)
    }
  }

  fun regenerateLastAiMessage() {
    val messages = _aiChatMessages.value
    if (messages.isEmpty()) return
    val lastUserMsgIndex = messages.indexOfLast { it.isUser }
    if (lastUserMsgIndex >= 0) {
      val userPrompt = messages[lastUserMsgIndex].text
      _aiChatMessages.value = messages.take(lastUserMsgIndex)
      sendCoachPrompt(userPrompt)
    }
  }

  fun createNewChatSession() {
    stopAiGeneration()
    _aiChatMessages.value = emptyList()
    _currentSessionId.value = null
  }

  fun loadChatSession(sessionId: String) {
    stopAiGeneration()
    _currentSessionId.value = sessionId
    viewModelScope.launch {
      val msgs = repository.getMessagesForSession(sessionId).first()
      if (_currentSessionId.value == sessionId) {
        _aiChatMessages.value = msgs
      }
    }
  }

  fun deleteChatSession(sessionId: String) {
    viewModelScope.launch {
      repository.deleteSession(sessionId)
      if (_currentSessionId.value == sessionId) {
        createNewChatSession()
      }
    }
  }

  fun clearAllChatHistory() {
    viewModelScope.launch {
      repository.clearAllChatHistory()
      createNewChatSession()
    }
  }

  // AI Memory Management
  fun explicitRemember(text: String) {
    viewModelScope.launch {
      val memory = com.example.model.AiMemory(
        id = UUID.randomUUID().toString(),
        text = text,
        category = "Preference",
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
      )
      repository.insertMemory(memory)
    }
  }

  fun deleteMemory(memoryId: String) {
    viewModelScope.launch {
      repository.deleteMemory(memoryId)
    }
  }

  fun clearAllMemories() {
    viewModelScope.launch {
      repository.clearAllMemories()
    }
  }

  fun clearAiChatSession() {
    stopAiGeneration()
    _aiChatMessages.value = emptyList()
    _currentSessionId.value = null
  }
}
