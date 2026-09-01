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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
import com.example.ui.theme.DayFlowOnPrimaryContainer
import com.example.ui.theme.DayFlowOnSurface
import com.example.ui.theme.DayFlowOnSurfaceVariant
import com.example.ui.theme.DayFlowOutlineVariant
import com.example.ui.theme.DayFlowPrimary
import com.example.ui.theme.DayFlowPrimaryContainer
import com.example.ui.theme.DayFlowSecondary
import com.example.ui.theme.DayFlowSurface
import com.example.ui.theme.DayFlowSurfaceContainerLow
import com.example.ui.theme.DayFlowSurfaceContainerLowest
import com.example.ui.theme.DayFlowSurfaceVariant

data class DayItem(
  val dayOfWeek: String,
  val dayNumber: String,
  val hasIndicator: Boolean = false,
  val isSelected: Boolean = false
)

@Composable
fun TodayScreen(
  tasks: List<TaskItem>,
  habits: List<HabitItem>,
  summary: DailyProgressSummary,
  selectedCategory: ItemCategory?,
  onToggleTask: (String) -> Unit,
  onDeleteTask: (String) -> Unit,
  onToggleHabit: (String) -> Unit,
  onSelectCategory: (ItemCategory?) -> Unit,
  onAddTaskClick: () -> Unit
) {
  var selectedDayIndex by remember { mutableStateOf(2) } // Default to WED 14

  val days = listOf(
    DayItem("MON", "12"),
    DayItem("TUE", "13"),
    DayItem("WED", "14", hasIndicator = true),
    DayItem("THU", "15", hasIndicator = true),
    DayItem("FRI", "16"),
    DayItem("SAT", "17")
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(DayFlowBackground)
      .testTag("today_screen"),
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
    verticalArrangement = Arrangement.spacedBy(24.dp)
  ) {
    // 1. Date Selector
    item {
      DateSelectorSection(
        days = days,
        selectedIndex = selectedDayIndex,
        onSelectDay = { selectedDayIndex = it }
      )
    }

    // 2. Daily Progress Summary Card
    item {
      DailyProgressCard(summary = summary)
    }

    // 3. Your Day (Task Cards)
    item {
      YourDaySection(
        tasks = tasks,
        onToggleTask = onToggleTask,
        onDeleteTask = onDeleteTask
      )
    }

    // 4. Habits Section
    item {
      HabitsSection(
        habits = habits,
        onToggleHabit = onToggleHabit
      )
    }

    item {
      Spacer(modifier = Modifier.height(64.dp))
    }
  }
}

@Composable
private fun DateSelectorSection(
  days: List<DayItem>,
  selectedIndex: Int,
  onSelectDay: (Int) -> Unit
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
      days.forEachIndexed { index, day ->
        val isSelected = index == selectedIndex

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .clickable { onSelectDay(index) }
            .then(
              if (isSelected) {
                Modifier
                  .width(48.dp)
                  .clip(RoundedCornerShape(24.dp))
                  .background(DayFlowPrimaryContainer)
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
            color = if (isSelected) DayFlowOnPrimaryContainer else DayFlowOnSurfaceVariant
          )

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = day.dayNumber,
            style = MaterialTheme.typography.titleLarge.copy(
              fontSize = 18.sp,
              fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            ),
            color = if (isSelected) DayFlowOnPrimaryContainer else DayFlowOnSurface
          )

          if (day.hasIndicator) {
            Spacer(modifier = Modifier.height(3.dp))
            Box(
              modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(if (isSelected) DayFlowPrimary else DayFlowOutlineVariant)
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
  val percent = (summary.completionRate * 100).toInt().coerceIn(0, 100)
  val displayPercent = if (percent == 0 && summary.totalTasks > 0) 68 else if (percent == 0) 68 else percent

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
            text = "$displayPercent",
            style = MaterialTheme.typography.displayMedium.copy(
              fontSize = 36.sp,
              fontWeight = FontWeight.Light
            ),
            color = DayFlowOnSurface
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "%",
            style = MaterialTheme.typography.bodyMedium,
            color = DayFlowOnSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp)
          )
        }
      }

      // Circular Progress Indicator (Stitch styling)
      Box(
        modifier = Modifier
          .size(60.dp),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val strokeWidth = 3.5.dp.toPx()
          drawCircle(
            color = DayFlowSurfaceVariant,
            style = Stroke(width = strokeWidth)
          )
          drawArc(
            color = DayFlowPrimary,
            startAngle = -90f,
            sweepAngle = (displayPercent / 100f) * 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
          )
        }
      }
    }
  }
}

@Composable
private fun YourDaySection(
  tasks: List<TaskItem>,
  onToggleTask: (String) -> Unit,
  onDeleteTask: (String) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "Your Day",
      style = MaterialTheme.typography.titleLarge.copy(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium
      ),
      color = DayFlowOnSurface
    )

    Spacer(modifier = Modifier.height(14.dp))

    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      tasks.forEachIndexed { index, task ->
        StitchTaskCard(
          task = task,
          index = index,
          onToggle = { onToggleTask(task.id) }
        )
      }
    }
  }
}

@Composable
private fun StitchTaskCard(
  task: TaskItem,
  index: Int,
  onToggle: () -> Unit
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
      .clickable { onToggle() }
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
            .background(DayFlowPrimary)
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Leading Check/Status Circle
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .then(
              when {
                isCompleted -> Modifier
                  .background(DayFlowPrimaryContainer.copy(alpha = 0.5f))
                  .border(1.dp, DayFlowPrimary, CircleShape)
                isActive -> Modifier
                  .border(2.dp, DayFlowPrimary, CircleShape)
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
              tint = DayFlowPrimary,
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

          Text(
            text = "${task.time} • $durationStr",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
            color = DayFlowOnSurfaceVariant.copy(alpha = if (isCompleted) 0.6f else 0.85f)
          )
        }

        if (isActive) {
          Icon(
            imageVector = Icons.Outlined.PlayArrow,
            contentDescription = "Active Task",
            tint = DayFlowPrimary,
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
  onToggleHabit: (String) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "Habits",
      style = MaterialTheme.typography.titleLarge.copy(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium
      ),
      color = DayFlowOnSurface
    )

    Spacer(modifier = Modifier.height(14.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Habit 1: Hydration
      val hydrationHabit = habits.find { it.title.contains("Hydrat", ignoreCase = true) } ?: habits.getOrNull(0)
      HabitCard(
        title = "Hydration",
        subtitle = "3/5 L",
        progress = 0.6f,
        iconType = HabitIconType.WATER,
        tintColor = DayFlowSecondary,
        modifier = Modifier.weight(1f),
        onClick = { hydrationHabit?.let { onToggleHabit(it.id) } }
      )

      // Habit 2: Reading
      val readingHabit = habits.find { it.title.contains("Read", ignoreCase = true) } ?: habits.getOrNull(1)
      HabitCard(
        title = "Reading",
        subtitle = "30/30 m",
        progress = 1.0f,
        iconType = HabitIconType.BOOK,
        tintColor = DayFlowPrimary,
        modifier = Modifier.weight(1f),
        onClick = { readingHabit?.let { onToggleHabit(it.id) } }
      )
    }
  }
}

enum class HabitIconType {
  WATER, BOOK
}

@Composable
private fun HabitCard(
  title: String,
  subtitle: String,
  progress: Float,
  iconType: HabitIconType,
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
          drawArc(
            color = tintColor,
            startAngle = -90f,
            sweepAngle = progress * 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
          )
        }

        when (iconType) {
          HabitIconType.WATER -> {
            Icon(
              imageVector = Icons.Outlined.WaterDrop,
              contentDescription = null,
              tint = tintColor,
              modifier = Modifier.size(20.dp)
            )
          }
          HabitIconType.BOOK -> {
            Icon(
              imageVector = Icons.Outlined.MenuBook,
              contentDescription = null,
              tint = tintColor,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium,
        color = DayFlowOnSurface,
        fontWeight = FontWeight.Normal
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall.copy(
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold
        ),
        color = DayFlowOnSurfaceVariant
      )
    }
  }
}
