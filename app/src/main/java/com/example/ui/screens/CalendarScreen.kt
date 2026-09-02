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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ItemCategory
import com.example.model.TaskItem
import com.example.ui.theme.DayFlowBackground
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOnSecondaryContainer
import com.example.ui.theme.DayFlowOnSurface
import com.example.ui.theme.DayFlowOnSurfaceVariant
import com.example.ui.theme.DayFlowOutlineVariant
import com.example.ui.theme.DayFlowSecondaryContainer
import com.example.ui.theme.DayFlowSurfaceContainerLow
import com.example.ui.theme.DayFlowSurfaceContainerLowest
import com.example.ui.theme.DayFlowSurfaceVariant
import com.example.util.CalendarGridCell
import com.example.util.DateUtils

@Composable
fun CalendarScreen(
  tasks: List<TaskItem>,
  allTasks: List<TaskItem>,
  selectedDate: String,
  year: Int,
  month: Int,
  onSelectDate: (String) -> Unit,
  onPrevMonth: () -> Unit,
  onNextMonth: () -> Unit,
  onToggleTask: (String) -> Unit,
  onEditTask: (TaskItem) -> Unit,
  onAddTaskClick: () -> Unit
) {
  // Collect all dates that contain scheduled tasks to display indicators
  val scheduledDateKeys = remember(allTasks) {
    val todayKey = DateUtils.getTodayDateKey()
    allTasks.map { task ->
      if (task.dueDate == "Today") todayKey else task.dueDate
    }.toSet()
  }

  val monthGrid = remember(year, month, selectedDate, scheduledDateKeys) {
    DateUtils.buildMonthGrid(
      year = year,
      month = month,
      selectedDateKey = selectedDate,
      scheduledDateKeys = scheduledDateKeys
    )
  }

  val monthTitle = remember(year, month) {
    DateUtils.getMonthYearTitle(year, month)
  }

  val scheduleHeader = remember(selectedDate) {
    "Schedule for ${DateUtils.formatScheduleDate(selectedDate)}"
  }

  val itemsCountText = if (tasks.size == 1) "1 item" else "${tasks.size} items"

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(DayFlowBackground)
      .testTag("calendar_screen"),
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(24.dp)
  ) {
    // 1. Month Header & Navigation
    item(key = "calendar_month_header") {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("calendar_month_header"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = monthTitle,
          style = MaterialTheme.typography.titleMedium.copy(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
          ),
          color = DayFlowOnSurface,
          modifier = Modifier.testTag("calendar_month_title")
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          IconButton(
            onClick = onPrevMonth,
            modifier = Modifier
              .size(36.dp)
              .testTag("calendar_prev_month")
          ) {
            Icon(
              imageVector = Icons.Default.ChevronLeft,
              contentDescription = "Previous Month",
              tint = DayFlowOnSurfaceVariant
            )
          }
          IconButton(
            onClick = onNextMonth,
            modifier = Modifier
              .size(36.dp)
              .testTag("calendar_next_month")
          ) {
            Icon(
              imageVector = Icons.Default.ChevronRight,
              contentDescription = "Next Month",
              tint = DayFlowOnSurfaceVariant
            )
          }
        }
      }
    }

    // 2. Month Grid Card (Stitch Reference)
    item(key = "calendar_grid_card") {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("calendar_grid_card"),
        shape = RoundedCornerShape(16.dp),
        color = DayFlowSurfaceContainerLow,
        border = BorderStroke(1.dp, DayFlowCardBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          // Days of week header
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
              Text(
                text = day,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  letterSpacing = 0.5.sp
                ),
                color = DayFlowOnSurfaceVariant,
                modifier = Modifier.width(38.dp),
                textAlign = TextAlign.Center
              )
            }
          }

          // Dynamic weeks of month
          monthGrid.forEach { week ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
              horizontalArrangement = Arrangement.SpaceAround
            ) {
              week.forEach { cell ->
                CalendarDayCell(
                  cell = cell,
                  onClick = { onSelectDate(cell.dateKey) }
                )
              }
            }
          }
        }
      }
    }

    // 3. Schedule Section Header
    item(key = "calendar_schedule_header") {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("calendar_schedule_header")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          Text(
            text = scheduleHeader,
            style = MaterialTheme.typography.titleMedium.copy(
              fontSize = 18.sp,
              fontWeight = FontWeight.Medium
            ),
            color = DayFlowOnSurface,
            modifier = Modifier.testTag("schedule_header_text")
          )
          Text(
            text = itemsCountText,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
            color = DayFlowOnSurfaceVariant,
            modifier = Modifier.testTag("schedule_items_count")
          )
        }
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(DayFlowSurfaceVariant.copy(alpha = 0.8f))
        )
      }
    }

    // 4. Schedule Items or Empty State
    if (tasks.isEmpty()) {
      item(key = "calendar_empty_state") {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onAddTaskClick() }
            .testTag("calendar_empty_state"),
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
              color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(20.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = "No scheduled items",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
              ),
              color = DayFlowOnSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "Tap here or the + button to schedule a task for this date",
              style = MaterialTheme.typography.bodySmall,
              color = DayFlowOnSurfaceVariant,
              textAlign = TextAlign.Center
            )
          }
        }
      }
    } else {
      items(tasks, key = { it.id }) { task ->
        CalendarTaskCard(
          task = task,
          onToggle = { onToggleTask(task.id) },
          onClick = { onEditTask(task) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(72.dp))
    }
  }
}

@Composable
private fun CalendarDayCell(
  cell: CalendarGridCell,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .size(38.dp)
      .clip(CircleShape)
      .background(
        when {
          cell.isSelected -> MaterialTheme.colorScheme.primaryContainer
          else -> Color.Transparent
        }
      )
      .clickable { onClick() }
      .testTag("calendar_cell_${cell.dateKey}"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = cell.dayNumber.toString(),
        style = MaterialTheme.typography.bodySmall.copy(
          fontSize = 14.sp,
          fontWeight = if (cell.isSelected) FontWeight.SemiBold else FontWeight.Normal
        ),
        color = when {
          cell.isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
          !cell.isCurrentMonth -> DayFlowOnSurfaceVariant.copy(alpha = 0.35f)
          cell.isToday -> MaterialTheme.colorScheme.primary
          else -> DayFlowOnSurface
        }
      )

      if (cell.hasEvent) {
        val primaryColor = MaterialTheme.colorScheme.primary
        Box(
          modifier = Modifier
            .padding(top = 2.dp)
            .size(4.dp)
            .clip(CircleShape)
            .background(
              if (cell.isSelected) primaryColor else primaryColor.copy(alpha = 0.7f)
            )
        )
      } else {
        Spacer(modifier = Modifier.height(6.dp))
      }
    }
  }
}

@Composable
private fun CalendarTaskCard(
  task: TaskItem,
  onToggle: () -> Unit,
  onClick: () -> Unit
) {
  val isCompleted = task.isCompleted

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("calendar_task_${task.id}"),
    shape = RoundedCornerShape(16.dp),
    color = DayFlowSurfaceContainerLowest,
    border = BorderStroke(1.dp, DayFlowCardBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.Top
    ) {
      // Checkbox / Radio Icon (Clickable separately to toggle task)
      Box(
        modifier = Modifier
          .size(28.dp)
          .clip(CircleShape)
          .clickable { onToggle() }
          .testTag("task_toggle_${task.id}"),
        contentAlignment = Alignment.Center
      ) {
        if (isCompleted) {
          Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Completed",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
        } else {
          Icon(
            imageVector = Icons.Outlined.RadioButtonUnchecked,
            contentDescription = "Incomplete",
            tint = DayFlowOnSurfaceVariant,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = task.title,
            style = MaterialTheme.typography.bodyLarge.copy(
              fontSize = 16.sp,
              fontWeight = FontWeight.Normal,
              textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
            ),
            color = if (isCompleted) DayFlowOnSurfaceVariant.copy(alpha = 0.7f) else DayFlowOnSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
          )

          Spacer(modifier = Modifier.width(8.dp))

          val timeDisplay = if (!task.endTime.isNullOrBlank()) {
            "${task.time} - ${task.endTime}"
          } else {
            task.time
          }

          Text(
            text = timeDisplay,
            style = MaterialTheme.typography.bodySmall.copy(
              fontSize = 14.sp,
              fontWeight = FontWeight.Normal
            ),
            color = DayFlowOnSurfaceVariant
          )
        }

        if (task.description.isNotBlank()) {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = task.description,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
            color = if (isCompleted) DayFlowOnSurfaceVariant.copy(alpha = 0.6f) else DayFlowOnSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }

        // Duration or Category Tag
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (task.category == ItemCategory.WORK) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = DayFlowSecondaryContainer.copy(alpha = 0.6f)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Videocam,
                  contentDescription = null,
                  tint = DayFlowOnSecondaryContainer,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Work",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                  ),
                  color = DayFlowOnSecondaryContainer
                )
              }
            }
          } else {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = DayFlowSurfaceVariant.copy(alpha = 0.6f)
            ) {
              Text(
                text = task.category.displayName,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                ),
                color = DayFlowOnSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          if (task.estimatedMinutes > 0) {
            val h = task.estimatedMinutes / 60
            val m = task.estimatedMinutes % 60
            val durationStr = if (h > 0 && m > 0) "$h hr $m min" else if (h > 0) "$h hr" else "$m min"
            Text(
              text = "•  $durationStr",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
              color = DayFlowOnSurfaceVariant.copy(alpha = 0.8f)
            )
          }
        }
      }
    }
  }
}
