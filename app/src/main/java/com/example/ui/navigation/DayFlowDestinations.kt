package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.ui.graphics.vector.ImageVector

sealed class DayFlowDestination(
  val route: String,
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector
) {
  object Today : DayFlowDestination(
    route = "today",
    label = "Today",
    selectedIcon = Icons.Filled.CalendarToday,
    unselectedIcon = Icons.Outlined.CalendarToday
  )

  object Calendar : DayFlowDestination(
    route = "calendar",
    label = "Calendar",
    selectedIcon = Icons.Filled.CalendarMonth,
    unselectedIcon = Icons.Outlined.CalendarMonth
  )

  object Goals : DayFlowDestination(
    route = "goals",
    label = "Goals",
    selectedIcon = Icons.Filled.Flag,
    unselectedIcon = Icons.Outlined.Flag
  )

  object Statistics : DayFlowDestination(
    route = "statistics",
    label = "Stats",
    selectedIcon = Icons.Filled.Leaderboard,
    unselectedIcon = Icons.Outlined.Leaderboard
  )

  object Coach : DayFlowDestination(
    route = "coach",
    label = "AI",
    selectedIcon = Icons.Filled.SmartToy,
    unselectedIcon = Icons.Outlined.SmartToy
  )

  object Settings : DayFlowDestination(
    route = "settings",
    label = "Settings",
    selectedIcon = Icons.Filled.Settings,
    unselectedIcon = Icons.Outlined.Settings
  )

  companion object {
    val bottomNavItems: List<DayFlowDestination>
      get() = listOf(
        Today,
        Calendar,
        Goals,
        Statistics,
        Coach
      )
  }
}

