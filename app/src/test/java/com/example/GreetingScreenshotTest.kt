package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.DayFlowRepository
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.DayFlowTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val repository = DayFlowRepository()
    val tasks = repository.tasks.value
    val habits = repository.habits.value
    val summary = repository.getProgressSummary()

    composeTestRule.setContent {
      DayFlowTheme {
        TodayScreen(
          tasks = tasks,
          habits = habits,
          summary = summary,
          selectedDate = com.example.util.DateUtils.getTodayDateKey(),
          onSelectDate = {},
          selectedCategory = null,
          onToggleTask = {},
          onEditTask = {},
          onDeleteTask = {},
          onToggleHabit = {},
          onOpenHabitProgress = {},
          onAddHabitClick = {},
          onSelectCategory = {},
          onAddTaskClick = {}
        )
      }
    }

    composeTestRule.waitForIdle()

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
