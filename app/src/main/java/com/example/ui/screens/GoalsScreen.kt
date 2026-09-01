package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GoalItem
import com.example.model.ItemCategory
import com.example.ui.theme.DayFlowBackground
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOnPrimary
import com.example.ui.theme.DayFlowOnSurface
import com.example.ui.theme.DayFlowOnSurfaceVariant
import com.example.ui.theme.DayFlowPrimary
import com.example.ui.theme.DayFlowPrimaryContainer
import com.example.ui.theme.DayFlowSecondary
import com.example.ui.theme.DayFlowSecondaryContainer
import com.example.ui.theme.DayFlowSurface
import com.example.ui.theme.DayFlowSurfaceContainerHigh
import com.example.ui.theme.DayFlowSurfaceContainerLow
import com.example.ui.theme.DayFlowSurfaceContainerLowest
import com.example.ui.theme.DayFlowSurfaceVariant
import com.example.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
  goals: List<GoalItem> = emptyList(),
  onUpdateGoalProgress: (String, Int) -> Unit = { _, _ -> },
  onSetGoalProgress: (String, Int) -> Unit = { _, _ -> },
  onToggleGoalCompletion: (String) -> Unit = { _ -> },
  onUpdateGoal: (GoalItem) -> Unit = { _ -> },
  onDeleteGoal: (String) -> Unit = { _ -> },
  onAddGoal: (String, String, String, ItemCategory, Int, String, String, Int) -> Unit = { _, _, _, _, _, _, _, _ -> }
) {
  var showNewGoalSheet by remember { mutableStateOf(false) }
  var selectedGoalForDetail by remember { mutableStateOf<GoalItem?>(null) }
  var goalToEdit by remember { mutableStateOf<GoalItem?>(null) }
  var goalToDeleteId by remember { mutableStateOf<String?>(null) }

  val activeGoals = remember(goals) { goals.filter { !it.isCompleted } }
  val completedGoals = remember(goals) { goals.filter { it.isCompleted } }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(DayFlowBackground)
      .testTag("goals_screen"),
    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(28.dp)
  ) {
    // 1. Header Area with "+ NEW GOAL" Pill Button
    item(key = "goals_header") {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("goals_header"),
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
    item(key = "goals_active_header") {
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

    // Active Goal Cards or Empty State
    if (activeGoals.isEmpty()) {
      item(key = "goals_active_empty") {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { showNewGoalSheet = true }
            .testTag("goals_active_empty"),
          shape = RoundedCornerShape(14.dp),
          color = DayFlowSurfaceContainerLowest,
          border = BorderStroke(1.dp, DayFlowCardBorder)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 36.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Surface(
              modifier = Modifier.size(44.dp),
              shape = CircleShape,
              color = DayFlowPrimaryContainer.copy(alpha = 0.6f)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Flag,
                  contentDescription = null,
                  tint = DayFlowPrimary,
                  modifier = Modifier.size(20.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = "No active goals",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
              ),
              color = DayFlowOnSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "Tap + NEW GOAL to set your focus and track long-term progress",
              style = MaterialTheme.typography.bodySmall,
              color = DayFlowOnSurfaceVariant,
              textAlign = TextAlign.Center
            )
          }
        }
      }
    } else {
      items(activeGoals, key = { it.id }) { goal ->
        ActiveGoalCard(
          goal = goal,
          onClick = { selectedGoalForDetail = goal }
        )
      }
    }

    // 3. Completed Goals Section
    if (completedGoals.isNotEmpty()) {
      item(key = "goals_completed_header") {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "Completed",
            style = MaterialTheme.typography.titleMedium.copy(
              fontSize = 18.sp,
              fontWeight = FontWeight.Medium
            ),
            color = DayFlowOnSurfaceVariant.copy(alpha = 0.7f),
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

      items(completedGoals, key = { it.id }) { completed ->
        CompletedGoalCard(
          goal = completed,
          onClick = { selectedGoalForDetail = completed },
          onToggleReopen = { onToggleGoalCompletion(completed.id) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(72.dp))
    }
  }

  // Modal Sheet for Adding New Goal
  if (showNewGoalSheet) {
    NewGoalBottomSheet(
      onDismiss = { showNewGoalSheet = false },
      onCreate = { title, desc, tag, category, target, unit, deadline, initialProgress ->
        onAddGoal(title, desc, tag, category, target, unit, deadline, initialProgress)
        showNewGoalSheet = false
      }
    )
  }

  // Modal Sheet for Goal Progress & Management
  if (selectedGoalForDetail != null) {
    val goal = selectedGoalForDetail!!
    GoalDetailBottomSheet(
      goal = goal,
      onDismiss = { selectedGoalForDetail = null },
      onSetProgress = { newProgress ->
        onSetGoalProgress(goal.id, newProgress)
        selectedGoalForDetail = goal.copy(
          currentProgress = newProgress,
          isCompleted = newProgress >= goal.targetProgress
        )
      },
      onToggleCompletion = {
        onToggleGoalCompletion(goal.id)
        selectedGoalForDetail = null
      },
      onEditClick = {
        goalToEdit = goal
        selectedGoalForDetail = null
      },
      onDeleteClick = {
        goalToDeleteId = goal.id
        selectedGoalForDetail = null
      }
    )
  }

  // Modal Sheet for Editing Goal
  if (goalToEdit != null) {
    EditGoalBottomSheet(
      goal = goalToEdit!!,
      onDismiss = { goalToEdit = null },
      onUpdate = { updatedGoal ->
        onUpdateGoal(updatedGoal)
        goalToEdit = null
      }
    )
  }

  // Confirmation Dialog for Deletion
  if (goalToDeleteId != null) {
    AlertDialog(
      onDismissRequest = { goalToDeleteId = null },
      title = { Text("Delete Goal", style = MaterialTheme.typography.titleMedium) },
      text = { Text("Are you sure you want to delete this goal? This action cannot be undone.", style = MaterialTheme.typography.bodyMedium) },
      confirmButton = {
        TextButton(
          onClick = {
            goalToDeleteId?.let { onDeleteGoal(it) }
            goalToDeleteId = null
          }
        ) {
          Text("Delete", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = { goalToDeleteId = null }) {
          Text("Cancel", color = DayFlowOnSurfaceVariant)
        }
      },
      containerColor = DayFlowSurfaceContainerLowest,
      shape = RoundedCornerShape(16.dp)
    )
  }
}

@Composable
private fun ActiveGoalCard(
  goal: GoalItem,
  onClick: () -> Unit
) {
  val accentColor = if (goal.goalType == "SHORT TERM" || goal.category == ItemCategory.WORK) {
    DayFlowSecondary
  } else {
    DayFlowPrimary
  }

  val displayDaysLeft = remember(goal.deadline) {
    DateUtils.formatDaysLeft(goal.deadline)
  }

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable { onClick() }
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
          text = goal.goalType.uppercase(),
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
          ),
          color = accentColor
        )

        if (displayDaysLeft.isNotBlank()) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = DayFlowSurfaceContainerHigh
          ) {
            Text(
              text = displayDaysLeft,
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
              color = DayFlowOnSurfaceVariant,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
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

      if (goal.description.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = goal.description,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
          color = DayFlowOnSurfaceVariant,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
      }

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
          color = accentColor
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
            .fillMaxWidth(goal.progressFraction)
            .height(6.dp)
            .clip(CircleShape)
            .background(accentColor)
        )
      }
    }
  }
}

@Composable
private fun CompletedGoalCard(
  goal: GoalItem,
  onClick: () -> Unit,
  onToggleReopen: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
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
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.weight(1f, fill = false)
      ) {
        IconButton(
          onClick = onToggleReopen,
          modifier = Modifier.size(24.dp)
        ) {
          Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Completed Goal",
            tint = DayFlowPrimary,
            modifier = Modifier.size(22.dp)
          )
        }
        Text(
          text = goal.title,
          style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = 16.sp,
            textDecoration = TextDecoration.LineThrough
          ),
          color = DayFlowOnSurface.copy(alpha = 0.7f),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Text(
        text = if (goal.deadline.isNotBlank()) goal.deadline else "Completed",
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
        color = DayFlowOnSurfaceVariant
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalDetailBottomSheet(
  goal: GoalItem,
  onDismiss: () -> Unit,
  onSetProgress: (Int) -> Unit,
  onToggleCompletion: () -> Unit,
  onEditClick: () -> Unit,
  onDeleteClick: () -> Unit
) {
  var currentSliderProgress by remember(goal.currentProgress) {
    mutableFloatStateOf(goal.currentProgress.toFloat())
  }

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
      // Header with Title & Action Icons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = goal.goalType.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 1.sp
            ),
            color = if (goal.goalType == "SHORT TERM") DayFlowSecondary else DayFlowPrimary
          )
          Text(
            text = goal.title,
            style = MaterialTheme.typography.titleMedium.copy(
              fontSize = 20.sp,
              fontWeight = FontWeight.Medium
            ),
            color = DayFlowOnSurface
          )
        }

        Row {
          IconButton(onClick = onEditClick) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit Goal",
              tint = DayFlowOnSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
          }
          IconButton(onClick = onDeleteClick) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "Delete Goal",
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      if (goal.description.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = goal.description,
          style = MaterialTheme.typography.bodyMedium,
          color = DayFlowOnSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Progress Management Section
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = DayFlowSurfaceContainerLow,
        border = BorderStroke(1.dp, DayFlowCardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Progress",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
              color = DayFlowOnSurface
            )
            Text(
              text = "${currentSliderProgress.toInt()}%",
              style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
              ),
              color = DayFlowPrimary
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Slider(
            value = currentSliderProgress,
            onValueChange = { currentSliderProgress = it },
            onValueChangeFinished = {
              onSetProgress(currentSliderProgress.toInt())
            },
            valueRange = 0f..100f,
            steps = 19, // 5% increments
            colors = SliderDefaults.colors(
              thumbColor = DayFlowPrimary,
              activeTrackColor = DayFlowPrimary,
              inactiveTrackColor = DayFlowSurfaceVariant
            ),
            modifier = Modifier.testTag("goal_progress_slider")
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Quick Increment Chips
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(-10, -5, 5, 10, 25).forEach { delta ->
              val label = if (delta > 0) "+$delta%" else "$delta%"
              Surface(
                shape = CircleShape,
                color = DayFlowSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier
                  .weight(1f)
                  .clip(CircleShape)
                  .clickable {
                    val next = (currentSliderProgress.toInt() + delta).coerceIn(0, 100)
                    currentSliderProgress = next.toFloat()
                    onSetProgress(next)
                  }
              ) {
                Text(
                  text = label,
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                  color = DayFlowOnSurface,
                  modifier = Modifier.padding(vertical = 8.dp),
                  textAlign = TextAlign.Center
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Toggle Completed Status Button
      Button(
        onClick = onToggleCompletion,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
          containerColor = if (goal.isCompleted) DayFlowSurfaceVariant else DayFlowPrimary,
          contentColor = if (goal.isCompleted) DayFlowOnSurface else DayFlowOnPrimary
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("button_toggle_goal_completed")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = if (goal.isCompleted) Icons.Default.Replay else Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Text(
            text = if (goal.isCompleted) "Reopen Goal" else "Mark as Completed",
            fontWeight = FontWeight.Medium
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewGoalBottomSheet(
  onDismiss: () -> Unit,
  onCreate: (String, String, String, ItemCategory, Int, String, String, Int) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var tag by remember { mutableStateOf("LONG TERM") }
  var category by remember { mutableStateOf(ItemCategory.LEARNING) }
  var daysLeft by remember { mutableStateOf("180") }
  var initialProgress by remember { mutableIntStateOf(0) }

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
        placeholder = { Text("e.g. Learn Spanish Fluently") },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_new_goal_title"),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = DayFlowPrimary,
          unfocusedBorderColor = DayFlowCardBorder
        )
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Description (Optional)") },
        placeholder = { Text("e.g. Practice daily vocabulary and speaking") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = DayFlowPrimary,
          unfocusedBorderColor = DayFlowCardBorder
        )
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Goal Type Selector
      Text(
        text = "Goal Horizon",
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
        color = DayFlowOnSurfaceVariant
      )
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        listOf("LONG TERM", "SHORT TERM").forEach { type ->
          val isSelected = tag == type
          val activeColor = if (type == "SHORT TERM") DayFlowSecondary else DayFlowPrimary
          Surface(
            shape = CircleShape,
            color = if (isSelected) activeColor else DayFlowSurfaceContainerLow,
            border = BorderStroke(1.dp, if (isSelected) activeColor else DayFlowCardBorder),
            modifier = Modifier
              .weight(1f)
              .clip(CircleShape)
              .clickable {
                tag = type
                if (type == "SHORT TERM" && daysLeft == "180") daysLeft = "14"
                if (type == "LONG TERM" && daysLeft == "14") daysLeft = "180"
              }
          ) {
            Text(
              text = type,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
              color = if (isSelected) DayFlowOnPrimary else DayFlowOnSurfaceVariant,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(vertical = 10.dp),
              textAlign = TextAlign.Center
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Duration field
      OutlinedTextField(
        value = daysLeft,
        onValueChange = { daysLeft = it },
        label = { Text("Target Duration (Days Left)") },
        placeholder = { Text("e.g. 180") },
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
            val deadlineStr = "${daysLeft.trim()}d left"
            onCreate(title, description, tag, category, 100, "%", deadlineStr, initialProgress)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditGoalBottomSheet(
  goal: GoalItem,
  onDismiss: () -> Unit,
  onUpdate: (GoalItem) -> Unit
) {
  var title by remember { mutableStateOf(goal.title) }
  var description by remember { mutableStateOf(goal.description) }
  var tag by remember { mutableStateOf(goal.goalType) }
  var daysLeft by remember { mutableStateOf(goal.deadline.filter { it.isDigit() }.ifBlank { "30" }) }

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
        text = "Edit Goal",
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = DayFlowPrimary,
          unfocusedBorderColor = DayFlowCardBorder
        )
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Description") },
        modifier = Modifier.fillMaxWidth(),
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
        listOf("LONG TERM", "SHORT TERM").forEach { type ->
          val isSelected = tag == type
          val activeColor = if (type == "SHORT TERM") DayFlowSecondary else DayFlowPrimary
          Surface(
            shape = CircleShape,
            color = if (isSelected) activeColor else DayFlowSurfaceContainerLow,
            border = BorderStroke(1.dp, if (isSelected) activeColor else DayFlowCardBorder),
            modifier = Modifier
              .weight(1f)
              .clip(CircleShape)
              .clickable { tag = type }
          ) {
            Text(
              text = type,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
              color = if (isSelected) DayFlowOnPrimary else DayFlowOnSurfaceVariant,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(vertical = 10.dp),
              textAlign = TextAlign.Center
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      OutlinedTextField(
        value = daysLeft,
        onValueChange = { daysLeft = it },
        label = { Text("Days Left") },
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
            onUpdate(
              goal.copy(
                title = title.trim(),
                description = description.trim(),
                goalType = tag,
                deadline = "${daysLeft.trim()}d left"
              )
            )
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
      ) {
        Text("Update Goal", fontWeight = FontWeight.Medium)
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
