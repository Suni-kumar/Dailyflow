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
}

