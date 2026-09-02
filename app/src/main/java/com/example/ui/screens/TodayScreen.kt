package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyProgressSummary
import com.example.model.HabitItem
import com.example.model.ItemCategory
import com.example.model.TaskItem
import com.example.ui.theme.DayFlowBackground
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOnSurface
import com.example.ui.theme.DayFlowOnSurfaceVariant
import com.example.ui.theme.DayFlowOutlineVariant
import com.example.ui.theme.DayFlowSecondary
import com.example.ui.theme.DayFlowSurface
import com.example.ui.theme.DayFlowSurfaceContainerLow
import com.example.ui.theme.DayFlowSurfaceContainerLowest
import com.example.ui.theme.DayFlowSurfaceVariant
import com.example.util.DateUtils
import com.example.util.DayFlowDateItem

@Composable
fun TodayScreen(
  tasks: List<TaskItem>,
  habits: List<HabitItem>,
  summary: DailyProgressSummary,
  selectedDate: String,
  onSelectDate: (String) -> Unit,
  selectedCategory: ItemCategory?,
  onToggleTask: (String) -> Unit,
  onEditTask: (TaskItem) -> Unit,
  onDeleteTask: (String) -> Unit,
  onToggleHabit: (String) -> Unit,
  onOpenHabitProgress: (HabitItem) -> Unit,
  onAddHabitClick: () -> Unit,
  onSelectCategory: (ItemCategory?) -> Unit,
  onAddTaskClick: () -> Unit
) {
  val weekDays = remember(selectedDate) {
    DateUtils.getCurrentWeekDays(selectedDate)
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(DayFlowBackground)
      .testTag("today_screen"),
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
    verticalArrangement = Arrangement.spacedBy(24.dp)
  ) {
    // 1. Date Selector
    item(key = "date_selector") {
      DateSelectorSection(
        days = weekDays,
        selectedDate = selectedDate,
        onSelectDate = onSelectDate
      )
    }

    // 2. Daily Progress Summary Card
    item(key = "daily_summary") {
      DailyProgressCard(summary = summary)
    }

    // 3. Your Day (Task Cards)
    item(key = "your_day_section") {
      YourDaySection(
        tasks = tasks,
        onToggleTask = onToggleTask,
        onEditTask = onEditTask,
        onAddTaskClick = onAddTaskClick
      )
    }

    // 4. Habits Section
    item(key = "habits_section") {
      HabitsSection(
        habits = habits,
        onToggleHabit = onToggleHabit,
        onOpenHabitProgress = onOpenHabitProgress,
        onAddHabitClick = onAddHabitClick
      )
    }

    item(key = "bottom_spacer") {
      Spacer(modifier = Modifier.height(64.dp))
    }
  }
}

@Composable
private fun DateSelectorSection(
  days: List<DayFlowDateItem>,
  selectedDate: String,
  onSelectDate: (String) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      days.forEach { day ->
        val isSelected = day.dateKey == selectedDate

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .clickable { onSelectDate(day.dateKey) }
            .then(
              if (isSelected) {
                Modifier
                  .width(48.dp)
                  .clip(RoundedCornerShape(24.dp))
                  .background(MaterialTheme.colorScheme.primaryContainer)
                  .padding(vertical = 8.dp)
              } else {
                Modifier
                  .width(48.dp)
                  .padding(vertical = 8.dp)
              }
            )
            .testTag("date_selector_day_${day.dayOfWeek}")
        ) {
          Text(
            text = day.dayOfWeek,
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 0.5.sp
            ),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else DayFlowOnSurfaceVariant
          )

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = day.dayNumber,
            style = MaterialTheme.typography.titleLarge.copy(
              fontSize = 18.sp,
              fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            ),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else DayFlowOnSurface
          )

          if (day.isToday || day.hasIndicator) {
            Spacer(modifier = Modifier.height(3.dp))
            Box(
              modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(if (isSelected) MaterialTheme.colorScheme.primary else DayFlowOutlineVariant)
            )
          } else {
            Spacer(modifier = Modifier.height(7.dp))
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(DayFlowSurfaceVariant.copy(alpha = 0.6f))
    )
  }
}

@Composable
private fun DailyProgressCard(summary: DailyProgressSummary) {
  val percent = if (summary.totalTasks > 0) {
    (summary.completionRate * 100).toInt().coerceIn(0, 100)
  } else 0

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("daily_summary_card"),
    shape = RoundedCornerShape(16.dp),
    color = DayFlowSurfaceContainerLow,
    border = BorderStroke(1.dp, DayFlowCardBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 20.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Daily Progress",
          style = MaterialTheme.typography.bodyMedium,
          color = DayFlowOnSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.Bottom) {
          Text(
            text = "$percent",
            style = MaterialTheme.typography.displayMedium.copy(
              fontSize = 36.sp,
              fontWeight = FontWeight.Light
            ),
            color = DayFlowOnSurface,
            modifier = Modifier.testTag("daily_progress_percent")
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "%",
            style = MaterialTheme.typography.bodyMedium,
            color = DayFlowOnSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp)
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = if (summary.totalTasks > 0) "${summary.completedTasks} of ${summary.totalTasks} tasks completed" else "No tasks scheduled today",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
          color = DayFlowOnSurfaceVariant.copy(alpha = 0.75f)
        )
      }

      // Circular Progress Indicator
      val primaryColor = MaterialTheme.colorScheme.primary
      Box(
        modifier = Modifier.size(60.dp),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val strokeWidth = 3.5.dp.toPx()
          drawCircle(
            color = DayFlowSurfaceVariant,
            style = Stroke(width = strokeWidth)
          )
          if (percent > 0) {
            drawArc(
              color = primaryColor,
              startAngle = -90f,
              sweepAngle = (percent / 100f) * 360f,
              useCenter = false,
              style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun YourDaySection(
  tasks: List<TaskItem>,
  onToggleTask: (String) -> Unit,
  onEditTask: (TaskItem) -> Unit,
  onAddTaskClick: () -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Your Day",
        style = MaterialTheme.typography.titleLarge.copy(
          fontSize = 18.sp,
          fontWeight = FontWeight.Medium
        ),
        color = DayFlowOnSurface
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    if (tasks.isEmpty()) {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .clickable { onAddTaskClick() }
          .testTag("tasks_empty_state"),
        shape = RoundedCornerShape(12.dp),
        color = DayFlowSurfaceContainerLow.copy(alpha = 0.6f),
        border = BorderStroke(1.dp, DayFlowCardBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp, horizontal = 16.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "No tasks for this day",
            style = MaterialTheme.typography.bodyMedium,
            color = DayFlowOnSurface,
            fontWeight = FontWeight.Medium
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Tap here or the + button to add a task",
            style = MaterialTheme.typography.bodySmall,
            color = DayFlowOnSurfaceVariant
          )
        }
      }
    } else {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        tasks.forEachIndexed { index, task ->
          StitchTaskCard(
            task = task,
            index = index,
            onToggle = { onToggleTask(task.id) },
            onClick = { onEditTask(task) }
          )
        }
      }
    }
  }
}

@Composable
private fun StitchTaskCard(
  task: TaskItem,
  index: Int,
  onToggle: () -> Unit,
  onClick: () -> Unit
) {
  val isCompleted = task.isCompleted
  val isActive = !isCompleted && (index == 1 || (index == 0 && !isCompleted))

  val cardBg = when {
    isCompleted -> DayFlowSurfaceContainerLow
    isActive -> DayFlowSurfaceContainerLowest
    else -> DayFlowSurface
  }

  val borderCol = when {
    isActive -> DayFlowOutlineVariant
    else -> DayFlowCardBorder
  }

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("task_card_${task.id}"),
    shape = RoundedCornerShape(12.dp),
    color = cardBg,
    border = BorderStroke(1.dp, borderCol)
  ) {
    Box(modifier = Modifier.fillMaxWidth()) {
      // Left accent bar for active task
      if (isActive) {
        Box(
          modifier = Modifier
            .width(4.dp)
            .height(64.dp)
            .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
            .background(MaterialTheme.colorScheme.primary)
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Leading Check/Status Circle (clickable separately to toggle)
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .clickable { onToggle() }
            .then(
              when {
                isCompleted -> Modifier
                  .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                  .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                isActive -> Modifier
                  .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                else -> Modifier
                  .border(1.5.dp, DayFlowOutlineVariant, CircleShape)
              }
            ),
          contentAlignment = Alignment.Center
        ) {
          if (isCompleted) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Completed",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(14.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = task.title,
            style = MaterialTheme.typography.bodyLarge.copy(
              textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
            ),
            color = if (isCompleted) DayFlowOnSurfaceVariant.copy(alpha = 0.7f) else DayFlowOnSurface,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          Spacer(modifier = Modifier.height(2.dp))

          val durationStr = if (task.estimatedMinutes > 0) {
            val h = task.estimatedMinutes / 60
            val m = task.estimatedMinutes % 60
            if (h > 0 && m > 0) "$h hr $m min" else if (h > 0) "$h hours" else "$m min"
          } else "15 min"

          val timeRange = if (!task.endTime.isNullOrBlank()) {
            "${task.time} - ${task.endTime}"
          } else {
            task.time
          }

          Text(
            text = "$timeRange • $durationStr • ${task.category.displayName}",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
            color = DayFlowOnSurfaceVariant.copy(alpha = if (isCompleted) 0.6f else 0.85f)
          )
        }

        if (isActive) {
          Icon(
            imageVector = Icons.Outlined.PlayArrow,
            contentDescription = "Active Task",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun HabitsSection(
  habits: List<HabitItem>,
  onToggleHabit: (String) -> Unit,
  onOpenHabitProgress: (HabitItem) -> Unit,
  onAddHabitClick: () -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Habits",
        style = MaterialTheme.typography.titleLarge.copy(
          fontSize = 18.sp,
          fontWeight = FontWeight.Medium
        ),
        color = DayFlowOnSurface
      )

      IconButton(
        onClick = onAddHabitClick,
        modifier = Modifier
          .size(32.dp)
          .testTag("add_habit_button")
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "Add Habit",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    if (habits.isEmpty()) {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .clickable { onAddHabitClick() }
          .testTag("habits_empty_state"),
        shape = RoundedCornerShape(12.dp),
        color = DayFlowSurfaceContainerLow.copy(alpha = 0.6f),
        border = BorderStroke(1.dp, DayFlowCardBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 16.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Text(
            text = "No habits tracked yet",
            style = MaterialTheme.typography.bodyMedium,
            color = DayFlowOnSurface,
            fontWeight = FontWeight.Medium
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Tap + to build your daily habits",
            style = MaterialTheme.typography.bodySmall,
            color = DayFlowOnSurfaceVariant
          )
        }
      }
    } else {
      // Habit cards displayed in responsive row/chunked rows
      val habitChunks = habits.chunked(2)
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        habitChunks.forEach { rowHabits ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            rowHabits.forEach { habit ->
              val isMeasurable = habit.dailyTarget > 1 || habit.unit.isNotBlank()
              val subtitle = if (isMeasurable) {
                "${habit.currentProgress}/${habit.dailyTarget} ${habit.unit}".trim()
              } else {
                if (habit.completedToday) "Completed" else "Tap to complete"
              }

              val (icon, tint) = resolveHabitVisuals(habit)

              HabitCard(
                title = habit.title,
                subtitle = subtitle,
                progress = habit.progressFraction,
                icon = icon,
                tintColor = tint,
                modifier = Modifier.weight(1f),
                onClick = {
                  if (isMeasurable) {
                    onOpenHabitProgress(habit)
                  } else {
                    onToggleHabit(habit.id)
                  }
                }
              )
            }

            if (rowHabits.size == 1) {
              Spacer(modifier = Modifier.weight(1f))
            }
          }
        }
      }
    }
  }
}

@Composable
private fun resolveHabitVisuals(habit: HabitItem): Pair<ImageVector, Color> {
  val title = habit.title.lowercase()
  val primary = MaterialTheme.colorScheme.primary
  val secondary = MaterialTheme.colorScheme.secondary
  return when {
    title.contains("water") || title.contains("hydrat") || title.contains("drink") ->
      Icons.Outlined.WaterDrop to secondary
    title.contains("read") || title.contains("book") || title.contains("study") ->
      Icons.Outlined.MenuBook to primary
    title.contains("run") || title.contains("gym") || title.contains("workout") || title.contains("fit") ->
      Icons.Outlined.FitnessCenter to Color(0xFFEF4444)
    title.contains("meditat") || title.contains("mind") || title.contains("breath") ->
      Icons.Outlined.SelfImprovement to Color(0xFF06B6D4)
    habit.category == ItemCategory.WORK ->
      Icons.Outlined.WorkOutline to Color(0xFF3B82F6)
    habit.category == ItemCategory.HEALTH ->
      Icons.Outlined.FavoriteBorder to Color(0xFF10B981)
    else ->
      Icons.Outlined.CheckCircleOutline to primary
  }
}

@Composable
private fun HabitCard(
  title: String,
  subtitle: String,
  progress: Float,
  icon: ImageVector,
  tintColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Surface(
    modifier = modifier
      .clickable { onClick() }
      .testTag("habit_card_${title.lowercase()}"),
    shape = RoundedCornerShape(16.dp),
    color = DayFlowSurfaceContainerLow,
    border = BorderStroke(1.dp, DayFlowCardBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 18.dp, horizontal = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier.size(48.dp),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val strokeWidth = 2.5.dp.toPx()
          drawCircle(
            color = DayFlowSurfaceVariant,
            style = Stroke(width = strokeWidth)
          )
          if (progress > 0f) {
            drawArc(
              color = tintColor,
              startAngle = -90f,
              sweepAngle = progress * 360f,
              useCenter = false,
              style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
          }
        }

        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = tintColor,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium,
        color = DayFlowOnSurface,
        fontWeight = FontWeight.Normal,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall.copy(
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold
        ),
        color = DayFlowOnSurfaceVariant,
        textAlign = TextAlign.Center
      )
    }
  }
}
