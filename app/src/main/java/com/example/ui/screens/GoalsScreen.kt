package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GoalItem
import com.example.ui.theme.DayFlowBackground
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOnPrimary
import com.example.ui.theme.DayFlowOnSurface
import com.example.ui.theme.DayFlowOnSurfaceVariant
import com.example.ui.theme.DayFlowOutline
import com.example.ui.theme.DayFlowOutlineVariant
import com.example.ui.theme.DayFlowPrimary
import com.example.ui.theme.DayFlowPrimaryContainer
import com.example.ui.theme.DayFlowSecondary
import com.example.ui.theme.DayFlowSecondaryContainer
import com.example.ui.theme.DayFlowSurface
import com.example.ui.theme.DayFlowSurfaceContainerHigh
import com.example.ui.theme.DayFlowSurfaceContainerLow
import com.example.ui.theme.DayFlowSurfaceContainerLowest
import com.example.ui.theme.DayFlowSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
  goals: List<GoalItem> = emptyList(),
  onUpdateGoalProgress: (String, Int) -> Unit = { _, _ -> },
  onAddGoal: (String, com.example.model.ItemCategory, Int, String, String) -> Unit = { _, _, _, _, _ -> }
) {
  var showNewGoalSheet by remember { mutableStateOf(false) }

  val activeGoals = remember {
    listOf(
      GoalDisplayData(
        id = "1",
        tag = "LONG TERM",
        title = "Learn Spanish Fluently",
        daysLeft = "180d left",
        progressPercentage = 35,
        accentColor = DayFlowPrimary
      ),
      GoalDisplayData(
        id = "2",
        tag = "SHORT TERM",
        title = "Launch Portfolio Website",
        daysLeft = "14d left",
        progressPercentage = 80,
        accentColor = DayFlowSecondary
      )
    )
  }

  val completedGoals = remember {
    listOf(
      CompletedGoalData(
        id = "3",
        title = "Read 12 Books",
        completionDate = "Dec 2023"
      )
    )
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(DayFlowBackground)
      .testTag("goals_screen"),
    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(28.dp)
  ) {
    // 1. Header Area with "+ NEW GOAL" Pill Button
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Column {
          Text(
            text = "Goals",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontSize = 28.sp,
              fontWeight = FontWeight.Normal,
              letterSpacing = (-0.3).sp
            ),
            color = DayFlowOnSurface
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Focus on what matters.",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = DayFlowOnSurfaceVariant
          )
        }

        // Pill Button
        Button(
          onClick = { showNewGoalSheet = true },
          shape = CircleShape,
          colors = ButtonDefaults.buttonColors(
            containerColor = DayFlowPrimary,
            contentColor = DayFlowOnPrimary
          ),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
          modifier = Modifier.testTag("button_new_goal")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "NEW GOAL",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
              )
            )
          }
        }
      }
    }

    // 2. Active Goals Section
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Active",
          style = MaterialTheme.typography.titleMedium.copy(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
          ),
          color = DayFlowOnSurface,
          modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(DayFlowSurfaceVariant)
        )
      }
    }

    // Active Goal Cards
    items(activeGoals, key = { it.id }) { goal ->
      ActiveGoalCard(goal = goal)
    }

    // 3. Completed Goals Section
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Completed",
          style = MaterialTheme.typography.titleMedium.copy(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
          ),
          color = DayFlowOnSurfaceVariant.copy(alpha = 0.6f),
          modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(DayFlowSurfaceVariant)
        )
      }
    }

    // Completed Goal Items
    items(completedGoals, key = { it.id }) { completed ->
      CompletedGoalCard(goal = completed)
    }

    item {
      Spacer(modifier = Modifier.height(72.dp))
    }
  }

  // Modal Sheet for Adding New Goal
  if (showNewGoalSheet) {
    NewGoalBottomSheet(
      onDismiss = { showNewGoalSheet = false },
      onCreate = { title, tag, target, unit, deadline ->
        onAddGoal(title, tag, target, unit, deadline)
        showNewGoalSheet = false
      }
    )
  }
}

private data class GoalDisplayData(
  val id: String,
  val tag: String,
  val title: String,
  val daysLeft: String,
  val progressPercentage: Int,
  val accentColor: Color
)

private data class CompletedGoalData(
  val id: String,
  val title: String,
  val completionDate: String
)

@Composable
private fun ActiveGoalCard(goal: GoalDisplayData) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("goal_card_${goal.id}"),
    shape = RoundedCornerShape(16.dp),
    color = DayFlowSurfaceContainerLow,
    border = BorderStroke(1.dp, DayFlowSurfaceVariant)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      // Top Row: Tag + Days Left Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = goal.tag,
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
          ),
          color = goal.accentColor
        )

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = DayFlowSurfaceContainerHigh
        ) {
          Text(
            text = goal.daysLeft,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
            color = DayFlowOnSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Title
      Text(
        text = goal.title,
        style = MaterialTheme.typography.titleMedium.copy(
          fontSize = 18.sp,
          fontWeight = FontWeight.Medium
        ),
        color = DayFlowOnSurface
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Progress Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Progress",
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
          color = DayFlowOnSurfaceVariant
        )
        Text(
          text = "${goal.progressPercentage}%",
          style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
          ),
          color = goal.accentColor
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Progress Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(CircleShape)
          .background(DayFlowSurfaceVariant)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth(goal.progressPercentage / 100f)
            .height(6.dp)
            .clip(CircleShape)
            .background(goal.accentColor)
        )
      }
    }
  }
}

@Composable
private fun CompletedGoalCard(goal: CompletedGoalData) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("completed_goal_${goal.id}"),
    shape = RoundedCornerShape(12.dp),
    color = DayFlowBackground,
    border = BorderStroke(1.dp, DayFlowSurfaceVariant)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Icon(
          imageVector = Icons.Filled.CheckCircle,
          contentDescription = "Completed",
          tint = DayFlowPrimary,
          modifier = Modifier.size(22.dp)
        )
        Text(
          text = goal.title,
          style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = 16.sp,
            textDecoration = TextDecoration.LineThrough
          ),
          color = DayFlowOnSurface.copy(alpha = 0.7f)
        )
      }

      Text(
        text = goal.completionDate,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
        color = DayFlowOnSurfaceVariant
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewGoalBottomSheet(
  onDismiss: () -> Unit,
  onCreate: (String, com.example.model.ItemCategory, Int, String, String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var tag by remember { mutableStateOf("SHORT TERM") }
  var daysLeft by remember { mutableStateOf("30") }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    containerColor = DayFlowSurfaceContainerLowest
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
      Text(
        text = "Create New Goal",
        style = MaterialTheme.typography.titleMedium.copy(
          fontSize = 18.sp,
          fontWeight = FontWeight.Medium
        ),
        color = DayFlowOnSurface
      )
      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        label = { Text("Goal Title") },
        placeholder = { Text("e.g. Read 10 Books") },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_new_goal_title"),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = DayFlowPrimary,
          unfocusedBorderColor = DayFlowCardBorder
        )
      )

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        listOf("SHORT TERM", "LONG TERM").forEach { type ->
          val isSelected = tag == type
          Surface(
            shape = CircleShape,
            color = if (isSelected) DayFlowPrimary else DayFlowSurfaceContainerLow,
            border = BorderStroke(1.dp, if (isSelected) DayFlowPrimary else DayFlowCardBorder),
            modifier = Modifier
              .weight(1f)
              .clickable { tag = type }
          ) {
            Text(
              text = type,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
              color = if (isSelected) DayFlowOnPrimary else DayFlowOnSurfaceVariant,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(vertical = 10.dp),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      OutlinedTextField(
        value = daysLeft,
        onValueChange = { daysLeft = it },
        label = { Text("Duration (Days Left)") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = DayFlowPrimary,
          unfocusedBorderColor = DayFlowCardBorder
        )
      )

      Spacer(modifier = Modifier.height(24.dp))

      Button(
        onClick = {
          if (title.isNotBlank()) {
            val category = if (tag == "LONG TERM") com.example.model.ItemCategory.LEARNING else com.example.model.ItemCategory.WORK
            onCreate(title, category, 100, "%", "${daysLeft}d left")
          }
        },
        enabled = title.isNotBlank(),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
          containerColor = DayFlowPrimary,
          contentColor = DayFlowOnPrimary
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("button_submit_goal")
      ) {
        Text("Save Goal", fontWeight = FontWeight.Medium)
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
