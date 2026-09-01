package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.DayFlowRepository
import com.example.data.local.DayFlowDatabase
import com.example.model.CalendarEventItem
import com.example.model.CoachInsight
import com.example.model.DailyProgressSummary
import com.example.model.GoalItem
import com.example.model.HabitItem
import com.example.model.InsightType
import com.example.model.ItemCategory
import com.example.model.TaskItem
import com.example.model.TaskPriority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class DayFlowViewModel(
  private val repository: DayFlowRepository = DayFlowRepository()
) : ViewModel() {

  companion object {
    fun provideFactory(context: android.content.Context): ViewModelProvider.Factory =
      object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          val database = DayFlowDatabase.getDatabase(context)
          val repository = DayFlowRepository.fromDatabase(database)
          return DayFlowViewModel(repository) as T
        }
      }
  }

  val tasks: StateFlow<List<TaskItem>> = repository.tasks
  val habits: StateFlow<List<HabitItem>> = repository.habits
  val goals: StateFlow<List<GoalItem>> = repository.goals
  val calendarEvents: StateFlow<List<CalendarEventItem>> = repository.calendarEvents
  val coachInsights: StateFlow<List<CoachInsight>> = repository.coachInsights

  private val _selectedCategory = MutableStateFlow<ItemCategory?>(null)
  val selectedCategory: StateFlow<ItemCategory?> = _selectedCategory.asStateFlow()

  private val _isAddTaskSheetOpen = MutableStateFlow(false)
  val isAddTaskSheetOpen: StateFlow<Boolean> = _isAddTaskSheetOpen.asStateFlow()

  private val _selectedCalendarDate = MutableStateFlow("Today")
  val selectedCalendarDate: StateFlow<String> = _selectedCalendarDate.asStateFlow()

  val progressSummary: StateFlow<DailyProgressSummary> = combine(tasks, habits) { taskList, habitList ->
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
    initialValue = repository.getProgressSummary()
  )

  fun toggleTask(taskId: String) {
    repository.toggleTask(taskId)
  }

  fun addTask(
    title: String,
    description: String,
    category: ItemCategory,
    priority: TaskPriority,
    time: String,
    estimatedMinutes: Int
  ) {
    if (title.isBlank()) return
    val newTask = TaskItem(
      id = UUID.randomUUID().toString(),
      title = title.trim(),
      description = description.trim(),
      category = category,
      priority = priority,
      time = if (time.isBlank()) "09:00 AM" else time,
      dueDate = "Today",
      isCompleted = false,
      estimatedMinutes = estimatedMinutes
    )
    repository.addTask(newTask)
    _isAddTaskSheetOpen.value = false
  }

  fun deleteTask(taskId: String) {
    repository.deleteTask(taskId)
  }

  fun toggleHabit(habitId: String) {
    repository.toggleHabit(habitId)
  }

  fun updateGoalProgress(goalId: String, increment: Int) {
    repository.updateGoalProgress(goalId, increment)
  }

  fun addGoal(
    title: String,
    category: ItemCategory,
    target: Int,
    unit: String,
    deadline: String
  ) {
    if (title.isBlank()) return
    val newGoal = GoalItem(
      id = UUID.randomUUID().toString(),
      title = title.trim(),
      category = category,
      currentProgress = 0,
      targetProgress = target.coerceAtLeast(1),
      unit = if (unit.isBlank()) "units" else unit.trim(),
      deadline = if (deadline.isBlank()) "In 30 days" else deadline.trim(),
      isCompleted = false
    )
    repository.addGoal(newGoal)
  }

  fun setCategoryFilter(category: ItemCategory?) {
    _selectedCategory.value = category
  }

  fun setSelectedCalendarDate(date: String) {
    _selectedCalendarDate.value = date
  }

  fun openAddTaskSheet() {
    _isAddTaskSheetOpen.value = true
  }

  fun closeAddTaskSheet() {
    _isAddTaskSheetOpen.value = false
  }

  fun sendCoachPrompt(prompt: String) {
    if (prompt.isBlank()) return
    viewModelScope.launch {
      val responseText = when {
        prompt.contains("focus", ignoreCase = true) ->
          "Based on your energy trends, you achieve highest focus between 9:00 AM and 11:30 AM. Tackle your high-priority items in this window!"
        prompt.contains("habit", ignoreCase = true) || prompt.contains("streak", ignoreCase = true) ->
          "You're currently on a 14-day streak for Hydration and 8-day streak for Meditation! Completing today's reading habit will keep your consistency index above 90%."
        prompt.contains("plan", ignoreCase = true) || prompt.contains("schedule", ignoreCase = true) ->
          "Your afternoon has a 2-hour clear focus block. I recommend finishing the DayFlow design review before 4 PM."
        else ->
          "Great question! Focusing on 3 high-impact tasks per day produces 2x better long-term goal completion than trying to do 10 small tasks."
      }

      val newInsight = CoachInsight(
        id = UUID.randomUUID().toString(),
        title = "AI Reflection & Advice",
        description = responseText,
        type = InsightType.ADVICE,
        timestamp = "Just now"
      )
      repository.addCoachInsight(newInsight)
    }
  }
}
