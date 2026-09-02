package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.AddHabitSheet
import com.example.ui.components.AddTaskSheet
import com.example.ui.components.DayFlowTopBar
import com.example.ui.components.HabitProgressSheet
import com.example.ui.navigation.DayFlowDestination
import com.example.ui.screens.AccentColorScreen
import com.example.ui.screens.AiCoachScreen
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.GoalsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.DayFlowBackground
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOnPrimary
import com.example.ui.theme.DayFlowOnSurfaceVariant
import com.example.ui.theme.DayFlowSurface
import com.example.ui.viewmodel.DayFlowViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun DayFlowApp(
  context: android.content.Context = androidx.compose.ui.platform.LocalContext.current,
  viewModel: DayFlowViewModel = viewModel(factory = DayFlowViewModel.provideFactory(context))
) {
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route ?: DayFlowDestination.Today.route

  val summary by viewModel.progressSummary.collectAsStateWithLifecycle()
  val selectedTodayDate by viewModel.selectedTodayDate.collectAsStateWithLifecycle()
  val selectedCalendarDate by viewModel.selectedCalendarDate.collectAsStateWithLifecycle()
  val isAddTaskSheetOpen by viewModel.isAddTaskSheetOpen.collectAsStateWithLifecycle()
  val editingTask by viewModel.editingTask.collectAsStateWithLifecycle()
  val isAddHabitSheetOpen by viewModel.isAddHabitSheetOpen.collectAsStateWithLifecycle()
  val editingHabit by viewModel.editingHabit.collectAsStateWithLifecycle()
  val habitForProgressSheet by viewModel.habitForProgressSheet.collectAsStateWithLifecycle()
  val customCategories by viewModel.customCategories.collectAsStateWithLifecycle()

  val isSettingsOrAccent = currentRoute == DayFlowDestination.Settings.route || currentRoute == DayFlowDestination.AccentColor.route

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(DayFlowBackground),
    containerColor = DayFlowBackground,
    topBar = {
      if (!isSettingsOrAccent) {
        DayFlowTopBar(
          title = "DayFlow",
          streakCount = summary.currentStreak,
          onProfileClick = {
            navController.navigate(DayFlowDestination.Settings.route) {
              launchSingleTop = true
            }
          },
          onSettingsClick = {
            navController.navigate(DayFlowDestination.Settings.route) {
              launchSingleTop = true
            }
          }
        )
      }
    },
    floatingActionButton = {
      if (currentRoute == DayFlowDestination.Today.route || currentRoute == DayFlowDestination.Calendar.route) {
        FloatingActionButton(
          onClick = {
            val targetDate = if (currentRoute == DayFlowDestination.Calendar.route) {
              selectedCalendarDate
            } else {
              selectedTodayDate
            }
            viewModel.openAddTaskSheet(targetDate)
          },
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = DayFlowOnPrimary,
          shape = CircleShape,
          elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp),
          modifier = Modifier
            .padding(bottom = 68.dp)
            .testTag("fab_add_task")
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Task",
            modifier = Modifier.size(24.dp)
          )
        }
      }
    },
    bottomBar = {
      if (!isSettingsOrAccent) {
        StitchBottomNavBar(
          currentRoute = currentRoute,
          onNavigate = { route ->
            navController.navigate(route) {
              popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
              }
              launchSingleTop = true
              restoreState = true
            }
          }
        )
      }
    }
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = DayFlowDestination.Today.route,
      modifier = Modifier
        .fillMaxSize()
        .background(DayFlowBackground)
        .padding(innerPadding)
    ) {
      composable(DayFlowDestination.Today.route) {
        val todayTasks by viewModel.todayTasks.collectAsStateWithLifecycle()
        val todayHabits by viewModel.todayHabits.collectAsStateWithLifecycle()
        val selectedDate by viewModel.selectedTodayDate.collectAsStateWithLifecycle()
        val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

        TodayScreen(
          tasks = todayTasks,
          habits = todayHabits,
          summary = summary,
          selectedDate = selectedDate,
          onSelectDate = { viewModel.selectTodayDate(it) },
          selectedCategory = selectedCategory,
          onToggleTask = { viewModel.toggleTask(it) },
          onEditTask = { viewModel.openEditTaskSheet(it) },
          onDeleteTask = { viewModel.deleteTask(it) },
          onToggleHabit = { viewModel.toggleHabit(it) },
          onOpenHabitProgress = { viewModel.openHabitProgressSheet(it) },
          onAddHabitClick = { viewModel.openAddHabitSheet() },
          onSelectCategory = { viewModel.setCategoryFilter(it) },
          onAddTaskClick = { viewModel.openAddTaskSheet(selectedDate) }
        )
      }

      composable(DayFlowDestination.Calendar.route) {
        val calendarTasks by viewModel.calendarTasks.collectAsStateWithLifecycle()
        val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
        val calendarDate by viewModel.selectedCalendarDate.collectAsStateWithLifecycle()
        val calendarYear by viewModel.calendarYear.collectAsStateWithLifecycle()
        val calendarMonth by viewModel.calendarMonth.collectAsStateWithLifecycle()

        CalendarScreen(
          tasks = calendarTasks,
          allTasks = allTasks,
          selectedDate = calendarDate,
          year = calendarYear,
          month = calendarMonth,
          onSelectDate = { viewModel.setSelectedCalendarDate(it) },
          onPrevMonth = { viewModel.prevCalendarMonth() },
          onNextMonth = { viewModel.nextCalendarMonth() },
          onToggleTask = { viewModel.toggleTask(it) },
          onEditTask = { viewModel.openEditTaskSheet(it) },
          onAddTaskClick = { viewModel.openAddTaskSheet(calendarDate) }
        )
      }

      composable(DayFlowDestination.Goals.route) {
        val goals by viewModel.goals.collectAsStateWithLifecycle()

        GoalsScreen(
          goals = goals,
          onUpdateGoalProgress = { id, inc -> viewModel.updateGoalProgress(id, inc) },
          onSetGoalProgress = { id, prog -> viewModel.setGoalProgress(id, prog) },
          onToggleGoalCompletion = { id -> viewModel.toggleGoalCompletion(id) },
          onUpdateGoal = { goal -> viewModel.updateGoal(goal) },
          onDeleteGoal = { id -> viewModel.deleteGoal(id) },
          onAddGoal = { title, desc, tag, cat, target, unit, deadline, initialProgress ->
            viewModel.addGoal(title, desc, tag, cat, target, unit, deadline, initialProgress)
          }
        )
      }

      composable(DayFlowDestination.Statistics.route) {
        val statisticsData by viewModel.statisticsData.collectAsStateWithLifecycle()
        val statsTimeRange by viewModel.statsTimeRange.collectAsStateWithLifecycle()
        val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()

        StatisticsScreen(
          statisticsData = statisticsData,
          selectedRange = statsTimeRange,
          onSelectRange = { viewModel.setStatsTimeRange(it) },
          onAddStreakDay = { viewModel.addStreakDay() },
          onRemoveStreakDay = { viewModel.removeStreakDay() },
          onResetStreak = { viewModel.resetStreak() },
          summary = summary,
          tasks = allTasks
        )
      }

      composable(DayFlowDestination.Coach.route) {
        val coachInsights by viewModel.coachInsights.collectAsStateWithLifecycle()
        val aiChatMessages by viewModel.aiChatMessages.collectAsStateWithLifecycle()
        val aiChatSessions by viewModel.aiChatSessions.collectAsStateWithLifecycle()
        val isAiThinking by viewModel.isAiThinking.collectAsStateWithLifecycle()
        val geminiApiKey by viewModel.geminiApiKey.collectAsStateWithLifecycle()
        val geminiConnectionVerified by viewModel.geminiConnectionVerified.collectAsStateWithLifecycle()

        AiCoachScreen(
          insights = coachInsights,
          summary = summary,
          chatMessages = aiChatMessages,
          chatSessions = aiChatSessions,
          isThinking = isAiThinking,
          isGeminiConfigured = geminiApiKey.isNotBlank(),
          isGeminiConnected = geminiApiKey.isNotBlank() && geminiConnectionVerified,
          onSendPrompt = { viewModel.sendCoachPrompt(it) },
          onTriggerAction = { viewModel.triggerCoachAction(it) },
          onStopGeneration = { viewModel.stopAiGeneration() },
          onRegenerate = { viewModel.regenerateLastAiMessage() },
          onRetry = { viewModel.retryLastAiMessage() },
          onClearChat = { viewModel.clearAiChatSession() },
          onNewChat = { viewModel.createNewChatSession() },
          onLoadSession = { viewModel.loadChatSession(it) },
          onDeleteSession = { viewModel.deleteChatSession(it) },
          onOpenSettings = {
            navController.navigate(DayFlowDestination.Settings.route) {
              launchSingleTop = true
            }
          }
        )
      }

      composable(DayFlowDestination.Settings.route) {
        val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
        val accentColor by viewModel.accentColor.collectAsStateWithLifecycle()
        val notifications by viewModel.notifications.collectAsStateWithLifecycle()
        val geminiApiKey by viewModel.geminiApiKey.collectAsStateWithLifecycle()
        val geminiConnectionVerified by viewModel.geminiConnectionVerified.collectAsStateWithLifecycle()
        val aiLanguage by viewModel.aiLanguage.collectAsStateWithLifecycle()
        val isTestingConnection by viewModel.isTestingConnection.collectAsStateWithLifecycle()
        val testConnectionResult by viewModel.testConnectionResult.collectAsStateWithLifecycle()
        val aiMemories by viewModel.aiMemories.collectAsStateWithLifecycle()

        SettingsScreen(
          themeMode = themeMode,
          onThemeModeChange = { viewModel.setThemeMode(it) },
          accentColor = accentColor,
          onOpenAccentColor = {
            navController.navigate(DayFlowDestination.AccentColor.route) {
              launchSingleTop = true
            }
          },
          notifications = notifications,
          onNotificationsEnabledChange = { viewModel.setNotificationsEnabled(it) },
          onMorningBriefingChange = { viewModel.setMorningBriefing(it) },
          onEveningReviewChange = { viewModel.setEveningReview(it) },
          onHabitRemindersChange = { viewModel.setHabitReminders(it) },
          geminiApiKey = geminiApiKey,
          isGeminiVerified = geminiConnectionVerified,
          onSaveGeminiApiKey = { viewModel.setGeminiApiKey(it) },
          onClearGeminiApiKey = { viewModel.clearGeminiApiKey() },
          aiLanguage = aiLanguage,
          onAiLanguageChange = { viewModel.setAiLanguage(it) },
          onTestGeminiConnection = { viewModel.testGeminiConnection(it) },
          isTestingConnection = isTestingConnection,
          testConnectionResult = testConnectionResult,
          onClearTestConnectionResult = { viewModel.clearTestConnectionResult() },
          aiMemories = aiMemories,
          onDeleteMemory = { viewModel.deleteMemory(it) },
          onClearAllMemories = { viewModel.clearAllMemories() },
          onClearAllChatHistory = { viewModel.clearAllChatHistory() },
          onExportBackup = { viewModel.exportBackupJson() },
          onImportBackup = { viewModel.importBackupJson(it) },
          onNavigateBack = { navController.popBackStack() }
        )
      }

      composable(DayFlowDestination.AccentColor.route) {
        val accentColor by viewModel.accentColor.collectAsStateWithLifecycle()

        AccentColorScreen(
          currentAccent = accentColor,
          onSelectAccent = { viewModel.setAccentColor(it) },
          onNavigateBack = { navController.popBackStack() }
        )
      }
    }
  }

  // Add Task Modal Bottom Sheet
  AddTaskSheet(
    isOpen = isAddTaskSheetOpen,
    customCategories = customCategories,
    onDismiss = { viewModel.closeAddTaskSheet() },
    onAddTask = { title, desc, cat, prio, time, duration ->
      viewModel.addTask(title, desc, cat, prio, time, duration)
    },
    onCreateCustomCategory = { viewModel.saveCustomCategory(it) }
  )

  // Edit Task Modal Bottom Sheet
  AddTaskSheet(
    isOpen = editingTask != null,
    taskToEdit = editingTask,
    customCategories = customCategories,
    onDismiss = { viewModel.closeEditTaskSheet() },
    onAddTask = { _, _, _, _, _, _ -> },
    onUpdateTask = { updatedTask -> viewModel.updateTask(updatedTask) },
    onDeleteTask = { taskId -> viewModel.deleteTask(taskId) },
    onCreateCustomCategory = { viewModel.saveCustomCategory(it) }
  )

  // Add / Edit Habit Modal Bottom Sheet
  AddHabitSheet(
    isOpen = isAddHabitSheetOpen || editingHabit != null,
    onDismiss = {
      if (editingHabit != null) {
        viewModel.closeEditHabitSheet()
      } else {
        viewModel.closeAddHabitSheet()
      }
    },
    onAddHabit = { title, category, dailyTarget, unit, reminderTime ->
      viewModel.addHabit(title, category, dailyTarget, unit, reminderTime)
    },
    habitToEdit = editingHabit,
    onUpdateHabit = { updatedHabit ->
      viewModel.updateHabit(updatedHabit)
    },
    onDeleteHabit = { habitId ->
      viewModel.deleteHabit(habitId)
    },
    customCategories = customCategories,
    onCreateCustomCategory = { viewModel.saveCustomCategory(it) }
  )

  // Habit Progress Modal Bottom Sheet
  HabitProgressSheet(
    habit = habitForProgressSheet,
    isOpen = habitForProgressSheet != null,
    onDismiss = { viewModel.closeHabitProgressSheet() },
    onUpdateProgress = { habitId, newProgress ->
      viewModel.updateHabitProgress(habitId, newProgress)
    },
    onDeleteHabit = { habitId ->
      viewModel.deleteHabit(habitId)
    },
    onEditHabit = { habit ->
      viewModel.openEditHabitSheet(habit)
    }
  )
}

@Composable
private fun StitchBottomNavBar(
  currentRoute: String,
  onNavigate: (String) -> Unit
) {
  Surface(
    color = DayFlowSurface.copy(alpha = 0.98f),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("bottom_nav_bar")
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Top Border
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(1.dp)
          .background(DayFlowCardBorder)
      )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .windowInsetsPadding(WindowInsets.navigationBars)
          .height(60.dp)
          .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        DayFlowDestination.bottomNavItems.forEach { destination ->
          val isSelected = currentRoute == destination.route
          StitchNavItem(
            destination = destination,
            isSelected = isSelected,
            onClick = { onNavigate(destination.route) },
            modifier = Modifier.weight(1f)
          )
        }
      }
    }
  }
}

@Composable
private fun StitchNavItem(
  destination: DayFlowDestination,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = modifier
      .clickable { onClick() }
      .padding(vertical = 6.dp)
      .testTag("nav_item_${destination.route}")
  ) {
    Icon(
      imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
      contentDescription = destination.label,
      tint = if (isSelected) MaterialTheme.colorScheme.primary else DayFlowOnSurfaceVariant.copy(alpha = 0.55f),
      modifier = Modifier.size(22.dp)
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = destination.label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 11.sp,
        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
      ),
      color = if (isSelected) MaterialTheme.colorScheme.primary else DayFlowOnSurfaceVariant.copy(alpha = 0.7f)
    )
  }
}
