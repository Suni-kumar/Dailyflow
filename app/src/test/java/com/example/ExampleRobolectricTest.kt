package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.DayFlowRepository
import com.example.model.GoalItem
import com.example.model.ItemCategory
import com.example.util.DateUtils
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("DayFlow", appName)
  }

  @Test
  fun `formatDaysLeft handles duration and dates properly`() {
    assertEquals("180d left", DateUtils.formatDaysLeft("180d left"))
    assertEquals("14d left", DateUtils.formatDaysLeft("14"))
    assertEquals("Dec 2023", DateUtils.formatDaysLeft("Dec 2023"))
  }

  @Test
  fun `goal progress and completion updates correctly`() = runBlocking {
    val repository = DayFlowRepository(coroutineScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Unconfined))
    val testGoal = GoalItem(
      id = "test_goal_1",
      title = "Master Kotlin & Jetpack Compose",
      description = "Build clean architectural features",
      goalType = "LONG TERM",
      category = ItemCategory.LEARNING,
      currentProgress = 35,
      targetProgress = 100,
      unit = "%",
      deadline = "180d left",
      isCompleted = false
    )
    repository.addGoal(testGoal)
    assertEquals(35, testGoal.progressPercentage)
    assertFalse(testGoal.isCompleted)

    repository.setGoalProgress("test_goal_1", 100)
    val updatedGoal = repository.goals.value.firstOrNull { it.id == "test_goal_1" }
    assertTrue(updatedGoal?.isCompleted == true)
  }

  @Test
  fun `calculateStatistics computes accurate real metrics`() = runBlocking {
    val repository = DayFlowRepository()
    val today = DateUtils.getTodayDateKey()
    val task1 = com.example.model.TaskItem(
      id = "stat_t1",
      title = "Architecture Review",
      category = ItemCategory.WORK,
      dueDate = today,
      isCompleted = true,
      estimatedMinutes = 60
    )
    val task2 = com.example.model.TaskItem(
      id = "stat_t2",
      title = "Evening Run",
      category = ItemCategory.FITNESS,
      dueDate = today,
      isCompleted = false,
      estimatedMinutes = 30
    )

    val stats7 = repository.calculateStatistics(
      com.example.model.StatsTimeRange.DAYS_7,
      listOf(task1, task2),
      emptyList()
    )

    assertEquals(1, stats7.tasksCompleted)
    assertEquals(2, stats7.tasksPlanned)
    assertEquals(50, stats7.completionRate)
    assertEquals(60, stats7.totalFocusMinutes)
    assertEquals(90, stats7.plannedFocusMinutes)
    assertTrue(stats7.hasAnyActivity)
    assertEquals(7, stats7.dailyStats.size)

    // Complete task 2
    val statsUpdated = repository.calculateStatistics(
      com.example.model.StatsTimeRange.DAYS_7,
      listOf(task1, task2.copy(isCompleted = true)),
      emptyList()
    )
    assertEquals(2, statsUpdated.tasksCompleted)
    assertEquals(100, statsUpdated.completionRate)
    assertEquals(90, statsUpdated.totalFocusMinutes)
  }

  @Test
  fun `dayflow context builder structures data accurately without leaking private keys`() {
    val context = com.example.data.ai.AiCoachContext(
      todayTasks = listOf(
        com.example.model.TaskItem(id = "1", title = "Write Design Doc", isCompleted = true, estimatedMinutes = 45),
        com.example.model.TaskItem(id = "2", title = "Review PR", isCompleted = false, estimatedMinutes = 30)
      ),
      todayHabits = listOf(
        com.example.model.HabitItem(id = "h1", title = "Hydration", streakDays = 5, dailyTarget = 8, currentProgress = 8, completedToday = true)
      ),
      activeGoals = listOf(
        com.example.model.GoalItem(id = "g1", title = "Learn Compose", currentProgress = 50, targetProgress = 100, deadline = "30d left")
      ),
      summary = com.example.model.DailyProgressSummary(2, 1, 1, 1, 45, 5),
      selectedDate = "2026-09-02"
    )

    val structured = com.example.data.ai.DayFlowContextBuilder.buildStructuredContext(context)
    assertTrue(structured.contains("1 of 2 completed"))
    assertTrue(structured.contains("Review PR"))
    assertTrue(structured.contains("Hydration"))
    assertTrue(structured.contains("Learn Compose"))
    assertTrue(structured.contains("Habit Streak: 5 days"))
    assertFalse(structured.contains("apiKey"))
  }

  @Test
  fun `ai service produces mindful fallback coaching in English and Hindi`() {
    val context = com.example.data.ai.AiCoachContext(
      todayTasks = listOf(
        com.example.model.TaskItem(id = "1", title = "Deep Work Block", isCompleted = false, estimatedMinutes = 60)
      ),
      todayHabits = emptyList(),
      activeGoals = emptyList(),
      summary = com.example.model.DailyProgressSummary(1, 0, 0, 0, 0, 3),
      selectedDate = "2026-09-02"
    )

    val englishBriefing = com.example.data.ai.DayFlowAiService.generateLocalOfflineCoaching(
      "Give me a morning briefing",
      context,
      com.example.data.local.AiLanguage.ENGLISH
    )
    assertTrue(englishBriefing.contains("Deep Work Block"))

    val hindiBriefing = com.example.data.ai.DayFlowAiService.generateLocalOfflineCoaching(
      "aaj ka plan kya hai",
      context,
      com.example.data.local.AiLanguage.HINDI
    )
    assertTrue(hindiBriefing.contains("Deep Work Block") || hindiBriefing.contains("tasks"))
  }

  @Test
  fun `user preferences manager safely persists and clears gemini api key and language`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefsManager = com.example.data.local.UserPreferencesManager(context)

    prefsManager.setGeminiApiKey("AIzaSyFakeKeyTest123")
    assertEquals("AIzaSyFakeKeyTest123", prefsManager.geminiApiKey.value)

    prefsManager.setAiLanguage(com.example.data.local.AiLanguage.HINDI)
    assertEquals(com.example.data.local.AiLanguage.HINDI, prefsManager.aiLanguage.value)

    prefsManager.clearGeminiApiKey()
    assertEquals("", prefsManager.geminiApiKey.value)
  }

  @Test
  fun `testConnection returns invalid key when key is empty or blank`() = runBlocking {
    val result = com.example.data.ai.DayFlowAiService.testConnection("   ")
    assertTrue(result is com.example.data.ai.ConnectionTestResult.InvalidKey)
  }

  @Test
  fun `context builder immediately reflects updated goal progress from 40 percent to 70 percent`() {
    val initialGoal = com.example.model.GoalItem(
      id = "g_port",
      title = "Launch Portfolio Website",
      currentProgress = 40,
      targetProgress = 100,
      unit = "%",
      deadline = "60d left"
    )

    val initialContext = com.example.data.ai.AiCoachContext(
      todayTasks = emptyList(),
      todayHabits = emptyList(),
      activeGoals = listOf(initialGoal),
      summary = com.example.model.DailyProgressSummary(0, 0, 0, 0, 0, 0),
      selectedDate = "2026-09-02",
      queryIntent = com.example.data.ai.CoachIntent.GOAL_GUIDANCE
    )

    val initialOutput = com.example.data.ai.DayFlowContextBuilder.buildStructuredContext(initialContext, "How is my portfolio goal?")
    assertTrue(initialOutput.contains("Launch Portfolio Website"))
    assertTrue(initialOutput.contains("40%"))
    assertFalse(initialOutput.contains("70%"))

    // User updates goal to 70%
    val updatedGoal = initialGoal.copy(currentProgress = 70)
    val updatedContext = initialContext.copy(activeGoals = listOf(updatedGoal))

    val updatedOutput = com.example.data.ai.DayFlowContextBuilder.buildStructuredContext(updatedContext, "How is my portfolio goal?")
    assertTrue(updatedOutput.contains("Launch Portfolio Website"))
    assertTrue(updatedOutput.contains("70%"))
    assertFalse(updatedOutput.contains("40%"))
  }

  @Test
  fun `context builder immediately reflects task completion and pending status`() {
    val task = com.example.model.TaskItem(
      id = "t_audit",
      title = "Architecture Refactoring",
      priority = com.example.model.TaskPriority.HIGH,
      time = "10:00 AM",
      isCompleted = false,
      estimatedMinutes = 90
    )

    val pendingContext = com.example.data.ai.AiCoachContext(
      todayTasks = listOf(task),
      todayHabits = emptyList(),
      activeGoals = emptyList(),
      summary = com.example.model.DailyProgressSummary(1, 0, 0, 0, 0, 0),
      selectedDate = "2026-09-02",
      queryIntent = com.example.data.ai.CoachIntent.DAILY_BRIEFING
    )

    val pendingOutput = com.example.data.ai.DayFlowContextBuilder.buildStructuredContext(pendingContext, "What should I do today?")
    assertTrue(pendingOutput.contains("Pending Tasks Remaining: 1 of 1"))
    assertTrue(pendingOutput.contains("Architecture Refactoring"))
    assertTrue(pendingOutput.contains("HIGH PRIORITY"))

    // User completes task
    val completedTask = task.copy(isCompleted = true)
    val completedContext = pendingContext.copy(
      todayTasks = listOf(completedTask),
      summary = com.example.model.DailyProgressSummary(1, 1, 0, 0, 90, 0)
    )

    val completedOutput = com.example.data.ai.DayFlowContextBuilder.buildStructuredContext(completedContext, "What should I do today?")
    assertTrue(completedOutput.contains("All planned tasks for today are already completed."))
  }

  @Test
  fun `smart intent detector accurately categorizes queries`() {
    assertEquals(
      com.example.data.ai.CoachIntent.DAILY_BRIEFING,
      com.example.data.ai.DayFlowContextBuilder.detectIntent("What should I focus on today?")
    )
    assertEquals(
      com.example.data.ai.CoachIntent.DAILY_BRIEFING,
      com.example.data.ai.DayFlowContextBuilder.detectIntent("Aaj mujhe kya karna chahiye?")
    )
    assertEquals(
      com.example.data.ai.CoachIntent.DAY_REVIEW,
      com.example.data.ai.DayFlowContextBuilder.detectIntent("How productive was today?")
    )
    assertEquals(
      com.example.data.ai.CoachIntent.GOAL_GUIDANCE,
      com.example.data.ai.DayFlowContextBuilder.detectIntent("Which goal needs the most attention?")
    )
    assertEquals(
      com.example.data.ai.CoachIntent.HABIT_STREAK,
      com.example.data.ai.DayFlowContextBuilder.detectIntent("Meri consistency kaisi hai?")
    )
  }
}


