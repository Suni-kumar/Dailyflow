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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.ui.components.AddTaskSheet
import com.example.ui.components.DayFlowTopBar
import com.example.ui.navigation.DayFlowDestination
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
import com.example.ui.theme.DayFlowPrimary
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

  val tasks by viewModel.tasks.collectAsStateWithLifecycle()
  val habits by viewModel.habits.collectAsStateWithLifecycle()
  val goals by viewModel.goals.collectAsStateWithLifecycle()
  val calendarEvents by viewModel.calendarEvents.collectAsStateWithLifecycle()
  val coachInsights by viewModel.coachInsights.collectAsStateWithLifecycle()
  val summary by viewModel.progressSummary.collectAsStateWithLifecycle()
  val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
  val isAddTaskSheetOpen by viewModel.isAddTaskSheetOpen.collectAsStateWithLifecycle()
  val selectedCalendarDate by viewModel.selectedCalendarDate.collectAsStateWithLifecycle()

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(DayFlowBackground),
    containerColor = DayFlowBackground,
    topBar = {
      if (currentRoute != DayFlowDestination.Settings.route) {
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
          onClick = { viewModel.openAddTaskSheet() },
          containerColor = DayFlowPrimary,
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
      if (currentRoute != DayFlowDestination.Settings.route) {
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
        TodayScreen(
          tasks = tasks,
          habits = habits,
          summary = summary,
          selectedCategory = selectedCategory,
          onToggleTask = { viewModel.toggleTask(it) },
          onDeleteTask = { viewModel.deleteTask(it) },
          onToggleHabit = { viewModel.toggleHabit(it) },
          onSelectCategory = { viewModel.setCategoryFilter(it) },
          onAddTaskClick = { viewModel.openAddTaskSheet() }
        )
      }

      composable(DayFlowDestination.Calendar.route) {
        CalendarScreen(
          events = calendarEvents,
          selectedDate = selectedCalendarDate,
          onSelectDate = { viewModel.setSelectedCalendarDate(it) }
        )
      }

      composable(DayFlowDestination.Goals.route) {
        GoalsScreen(
          goals = goals,
          onUpdateGoalProgress = { id, inc -> viewModel.updateGoalProgress(id, inc) },
          onAddGoal = { title, cat, target, unit, deadline ->
            viewModel.addGoal(title, cat, target, unit, deadline)
          }
        )
      }

      composable(DayFlowDestination.Statistics.route) {
        StatisticsScreen(
          summary = summary,
          tasks = tasks
        )
      }

      composable(DayFlowDestination.Coach.route) {
        AiCoachScreen(
          insights = coachInsights,
          summary = summary,
          onSendPrompt = { viewModel.sendCoachPrompt(it) }
        )
      }

      composable(DayFlowDestination.Settings.route) {
        SettingsScreen(
          onNavigateBack = { navController.popBackStack() }
        )
      }
    }
  }

  // Add Task Modal Bottom Sheet
  AddTaskSheet(
    isOpen = isAddTaskSheetOpen,
    onDismiss = { viewModel.closeAddTaskSheet() },
    onAddTask = { title, desc, cat, prio, time, duration ->
      viewModel.addTask(title, desc, cat, prio, time, duration)
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
      tint = if (isSelected) DayFlowPrimary else DayFlowOnSurfaceVariant.copy(alpha = 0.55f),
      modifier = Modifier.size(22.dp)
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = destination.label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 11.sp,
        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
      ),
      color = if (isSelected) DayFlowPrimary else DayFlowOnSurfaceVariant.copy(alpha = 0.7f)
    )
  }
}

