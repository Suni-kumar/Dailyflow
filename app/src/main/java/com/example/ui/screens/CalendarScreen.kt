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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalendarEventItem
import com.example.ui.theme.DayFlowBackground
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOnPrimary
import com.example.ui.theme.DayFlowOnSecondaryContainer
import com.example.ui.theme.DayFlowOnSurface
import com.example.ui.theme.DayFlowOnSurfaceVariant
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
import com.example.ui.theme.DayFlowTertiary
import com.example.ui.theme.DayFlowTertiaryContainer

data class CalendarScheduleItem(
  val id: String,
  val title: String,
  val time: String,
  val subtitle: String,
  val isCompleted: Boolean = false,
  val progress: Float? = null,
  val tag: String? = null
)

@Composable
fun CalendarScreen(
  events: List<CalendarEventItem> = emptyList(),
  selectedDate: String = "2023-10-12",
  onSelectDate: (String) -> Unit = {}
) {
  var selectedDay by remember { mutableIntStateOf(12) }

  val scheduleItems = remember {
    listOf(
      CalendarScheduleItem(
        id = "1",
        title = "Morning Reflection",
        time = "08:00",
        subtitle = "15 mins journaling",
        isCompleted = true
      ),
      CalendarScheduleItem(
        id = "2",
        title = "Deep Work Block",
        time = "10:00",
        subtitle = "Focus on Q4 strategic planning document.",
        isCompleted = false,
        progress = 0.45f
      ),
      CalendarScheduleItem(
        id = "3",
        title = "Client Call: Zenith Corp",
        time = "14:00",
        subtitle = "Review initial design proposals.",
        isCompleted = false,
        tag = "Zoom"
      ),
      CalendarScheduleItem(
        id = "4",
        title = "Evening Walk",
        time = "18:30",
        subtitle = "Disconnect and recharge.",
        isCompleted = false
      )
    )
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(DayFlowBackground)
      .testTag("calendar_screen"),
    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(28.dp)
  ) {
    // 1. Month Header & Navigation
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "October 2023",
          style = MaterialTheme.typography.titleMedium.copy(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
          ),
          color = DayFlowOnSurface
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          IconButton(
            onClick = {},
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ChevronLeft,
              contentDescription = "Previous Month",
              tint = DayFlowOnSurfaceVariant
            )
          }
          IconButton(
            onClick = {},
            modifier = Modifier.size(32.dp)
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
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = DayFlowSurfaceContainerLow,
        border = BorderStroke(1.dp, DayFlowSurfaceVariant)
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
                modifier = Modifier.width(36.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }

          // Week 1 (Previous month days)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            listOf(24, 25, 26, 27, 28, 29, 30).forEach { dayNum ->
              CalendarDayCell(
                dayNumber = dayNum,
                isCurrentMonth = false,
                isSelected = false,
                hasEvent = false,
                onClick = {}
              )
            }
          }

          // Week 2 (1 to 7)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            (1..7).forEach { dayNum ->
              CalendarDayCell(
                dayNumber = dayNum,
                isCurrentMonth = true,
                isSelected = selectedDay == dayNum,
                hasEvent = dayNum in listOf(2, 4, 7),
                onClick = { selectedDay = dayNum }
              )
            }
          }

          // Week 3 (8 to 14)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            (8..14).forEach { dayNum ->
              CalendarDayCell(
                dayNumber = dayNum,
                isCurrentMonth = true,
                isSelected = selectedDay == dayNum,
                hasEvent = dayNum in listOf(10, 12, 13),
                onClick = { selectedDay = dayNum }
              )
            }
          }

          // Week 4 (15 to 21)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            (15..21).forEach { dayNum ->
              CalendarDayCell(
                dayNumber = dayNum,
                isCurrentMonth = true,
                isSelected = selectedDay == dayNum,
                hasEvent = false,
                onClick = { selectedDay = dayNum }
              )
            }
          }
        }
      }
    }

    // 3. Schedule Section Header
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          Text(
            text = "Schedule for Oct $selectedDay",
            style = MaterialTheme.typography.titleMedium.copy(
              fontSize = 18.sp,
              fontWeight = FontWeight.Medium
            ),
            color = DayFlowOnSurface
          )
          Text(
            text = "4 items",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
            color = DayFlowOnSurfaceVariant
          )
        }
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(DayFlowSurfaceVariant)
        )
      }
    }

    // 4. Schedule Items
    items(scheduleItems, key = { it.id }) { item ->
      CalendarTaskCard(item = item)
    }

    item {
      Spacer(modifier = Modifier.height(72.dp))
    }
  }
}

@Composable
private fun CalendarDayCell(
  dayNumber: Int,
  isCurrentMonth: Boolean,
  isSelected: Boolean,
  hasEvent: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .size(36.dp)
      .clip(CircleShape)
      .background(
        if (isSelected) DayFlowPrimary.copy(alpha = 0.12f) else Color.Transparent
      )
      .clickable(enabled = isCurrentMonth) { onClick() },
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = dayNumber.toString(),
        style = MaterialTheme.typography.bodySmall.copy(
          fontSize = 14.sp,
          fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        ),
        color = when {
          !isCurrentMonth -> DayFlowOnSurfaceVariant.copy(alpha = 0.4f)
          isSelected -> DayFlowPrimary
          else -> DayFlowOnSurface
        }
      )
      if (hasEvent) {
        Box(
          modifier = Modifier
            .padding(top = 1.dp)
            .size(4.dp)
            .clip(CircleShape)
            .background(if (isSelected) DayFlowPrimary else DayFlowTertiaryContainer)
        )
      } else {
        Spacer(modifier = Modifier.height(5.dp))
      }
    }
  }
}

@Composable
private fun CalendarTaskCard(item: CalendarScheduleItem) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("calendar_task_${item.id}"),
    shape = RoundedCornerShape(16.dp),
    color = DayFlowSurfaceContainerLowest,
    border = BorderStroke(1.dp, DayFlowSurfaceVariant)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.Top
    ) {
      // Checkbox / Radio Icon
      if (item.isCompleted) {
        Icon(
          imageVector = Icons.Filled.CheckCircle,
          contentDescription = "Completed",
          tint = DayFlowPrimary,
          modifier = Modifier
            .size(24.dp)
            .padding(top = 2.dp)
        )
      } else {
        Icon(
          imageVector = Icons.Outlined.RadioButtonUnchecked,
          contentDescription = "Incomplete",
          tint = DayFlowOnSurfaceVariant,
          modifier = Modifier
            .size(24.dp)
            .padding(top = 2.dp)
        )
      }

      Spacer(modifier = Modifier.width(16.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = item.title,
            style = MaterialTheme.typography.bodyLarge.copy(
              fontSize = 16.sp,
              fontWeight = FontWeight.Normal,
              textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            ),
            color = if (item.isCompleted) DayFlowOnSurfaceVariant else DayFlowOnSurface
          )
          Text(
            text = item.time,
            style = MaterialTheme.typography.bodySmall.copy(
              fontSize = 14.sp,
              fontWeight = if (item.progress != null) FontWeight.Medium else FontWeight.Normal
            ),
            color = if (item.progress != null) DayFlowPrimary else DayFlowOnSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = item.subtitle,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
          color = if (item.isCompleted) DayFlowOnSurfaceVariant.copy(alpha = 0.6f) else DayFlowOnSurfaceVariant
        )

        // Optional Progress Bar
        if (item.progress != null) {
          Spacer(modifier = Modifier.height(12.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(CircleShape)
              .background(DayFlowSurfaceVariant)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth(item.progress)
                .height(6.dp)
                .clip(CircleShape)
                .background(DayFlowPrimary)
            )
          }
        }

        // Optional Tag (Zoom call)
        if (item.tag != null) {
          Spacer(modifier = Modifier.height(10.dp))
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
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = item.tag,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium
                ),
                color = DayFlowOnSecondaryContainer
              )
            }
          }
        }
      }
    }
  }
}
