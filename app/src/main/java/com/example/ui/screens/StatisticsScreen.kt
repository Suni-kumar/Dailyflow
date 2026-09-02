package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QueryBuilder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyActivityStat
import com.example.model.DailyProgressSummary
import com.example.model.ItemCategory
import com.example.model.StatisticsData
import com.example.model.StatsTimeRange
import com.example.model.TaskItem
import com.example.ui.theme.DayFlowBackground
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOnPrimary
import com.example.ui.theme.DayFlowOnSurface
import com.example.ui.theme.DayFlowOnSurfaceVariant
import com.example.ui.theme.DayFlowOutlineVariant
import com.example.ui.theme.DayFlowSecondary
import com.example.ui.theme.DayFlowSecondaryContainer
import com.example.ui.theme.DayFlowSecondaryFixed
import com.example.ui.theme.DayFlowSurface
import com.example.ui.theme.DayFlowSurfaceContainerLow
import com.example.ui.theme.DayFlowSurfaceContainerLowest
import com.example.ui.theme.DayFlowSurfaceVariant
import com.example.ui.theme.DayFlowTertiary
import com.example.ui.theme.DayFlowTertiaryContainer
import com.example.util.DateUtils

private enum class StreakConfirmAction {
  ADD_DAY,
  REMOVE_DAY,
  RESET
}

@Composable
fun StatisticsScreen(
  statisticsData: StatisticsData = StatisticsData(),
  selectedRange: StatsTimeRange = StatsTimeRange.DAYS_7,
  onSelectRange: (StatsTimeRange) -> Unit = {},
  onAddStreakDay: () -> Unit = {},
  onRemoveStreakDay: () -> Unit = {},
  onResetStreak: () -> Unit = {},
  summary: DailyProgressSummary = DailyProgressSummary(0, 0, 0, 0, 0, 0),
  tasks: List<TaskItem> = emptyList()
) {
  var pendingStreakAction by remember { mutableStateOf<StreakConfirmAction?>(null) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(DayFlowBackground)
      .testTag("statistics_screen"),
    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(24.dp)
  ) {
    // 1. Header & Time Range Filter Selector
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Statistics",
              style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 26.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = (-0.3).sp
              ),
              color = DayFlowOnSurface
            )
            Text(
              text = "Consistency & Deep Work",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
              color = DayFlowOnSurfaceVariant
            )
          }

          // Time Range Filter Segmented Control
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = DayFlowSurfaceContainerLow,
            border = BorderStroke(1.dp, DayFlowSurfaceVariant),
            modifier = Modifier.testTag("time_range_filter")
          ) {
            Row(
              modifier = Modifier.padding(3.dp),
              horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
              StatsTimeRange.values().forEach { range ->
                val isSelected = range == selectedRange
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onSelectRange(range) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("range_filter_${range.days}"),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = range.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                    ),
                    color = if (isSelected) Color.White else DayFlowOnSurfaceVariant
                  )
                }
              }
            }
          }
        }
      }
    }

    // 2. Empty State if no activity at all
    if (!statisticsData.hasAnyActivity && statisticsData.tasksPlanned == 0 && statisticsData.tasksCompleted == 0) {
      item {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .testTag("stats_empty_state"),
          shape = RoundedCornerShape(20.dp),
          color = DayFlowSurfaceContainerLow,
          border = BorderStroke(1.dp, DayFlowSurfaceVariant)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            Box(
              modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.TrendingUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
              )
            }

            Text(
              text = "Your statistics will appear here",
              style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
              ),
              color = DayFlowOnSurface,
              textAlign = TextAlign.Center
            )

            Text(
              text = "Start completing tasks and tracking habits in Today and Calendar to see your activity trends, deep focus time, and streak metrics.",
              style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 14.sp,
                lineHeight = 20.sp
              ),
              color = DayFlowOnSurfaceVariant,
              textAlign = TextAlign.Center
            )
          }
        }
      }
    } else {
      // 3. Overview Key Metrics (Tasks Completed, Planned, Completion Rate, Focus Time)
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Metric 1: Tasks Completed
          MetricTile(
            title = "Completed",
            value = "${statisticsData.tasksCompleted}",
            subtitle = "of ${statisticsData.tasksPlanned} planned",
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.CheckCircle,
            tint = MaterialTheme.colorScheme.primary
          )

          // Metric 2: Completion Rate
          MetricTile(
            title = "Completion",
            value = if (statisticsData.tasksPlanned > 0) "${statisticsData.completionRate}%" else "—",
            subtitle = if (statisticsData.tasksPlanned > 0) "success rate" else "No tasks",
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.TrendingUp,
            tint = MaterialTheme.colorScheme.secondary
          )

          // Metric 3: Deep Focus Time
          MetricTile(
            title = "Deep Focus",
            value = DateUtils.formatFocusMinutes(statisticsData.totalFocusMinutes),
            subtitle = "total focus logged",
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.QueryBuilder,
            tint = DayFlowTertiary
          )
        }
      }

      // 4. Activity Trends Bar Chart Card (Stitch Reference)
      item {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("stats_weekly_chart"),
          shape = RoundedCornerShape(16.dp),
          color = DayFlowSurfaceContainerLow,
          border = BorderStroke(1.dp, DayFlowSurfaceVariant)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Activity Trends",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Medium
                ),
                color = DayFlowOnSurface
              )

              Text(
                text = when (selectedRange) {
                  StatsTimeRange.DAYS_7 -> "LAST 7 DAYS"
                  StatsTimeRange.DAYS_14 -> "LAST 14 DAYS"
                  StatsTimeRange.DAYS_30 -> "LAST 30 DAYS"
                },
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  letterSpacing = 0.8.sp
                ),
                color = DayFlowOnSurfaceVariant
              )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (statisticsData.dailyStats.isEmpty() || statisticsData.dailyStats.all { it.totalTasks == 0 && it.completedTasks == 0 }) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(180.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "No activity recorded in this period",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                  color = DayFlowOnSurfaceVariant
                )
              }
            } else {
              // Bar chart view with responsive horizontal scroll for 14 / 30 days
              val scrollState = rememberScrollState()
              val chartModifier = if (selectedRange == StatsTimeRange.DAYS_7) {
                Modifier.fillMaxWidth()
              } else {
                Modifier.horizontalScroll(scrollState)
              }

              Row(
                modifier = chartModifier
                  .height(190.dp)
                  .padding(horizontal = 4.dp),
                horizontalArrangement = if (selectedRange == StatsTimeRange.DAYS_7) Arrangement.SpaceBetween else Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.Bottom
              ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                val secondaryColor = MaterialTheme.colorScheme.secondary
                statisticsData.dailyStats.forEach { stat ->
                  val barColor = when {
                    stat.isCurrentDay -> primaryColor
                    stat.completedTasks > 0 -> secondaryColor.copy(alpha = 0.85f)
                    stat.totalTasks > 0 -> DayFlowSurfaceVariant.copy(alpha = 0.8f)
                    else -> DayFlowSurfaceVariant.copy(alpha = 0.35f)
                  }

                  Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier
                      .then(if (selectedRange == StatsTimeRange.DAYS_7) Modifier.weight(1f) else Modifier.width(36.dp))
                      .fillMaxHeight()
                  ) {
                    // Tooltip / completed task count text above bar
                    if (stat.completedTasks > 0) {
                      Text(
                        text = "${stat.completedTasks}",
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold
                        ),
                        color = if (stat.isCurrentDay) primaryColor else secondaryColor,
                        modifier = Modifier.padding(bottom = 4.dp)
                      )
                    } else {
                      Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Bar Column
                    Box(
                      modifier = Modifier
                        .width(if (selectedRange == StatsTimeRange.DAYS_7) 26.dp else 20.dp)
                        .fillMaxHeight(stat.heightFraction.coerceIn(0.06f, 0.85f))
                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 3.dp, bottomEnd = 3.dp))
                        .background(barColor)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Day Label
                    Text(
                      text = if (selectedRange == StatsTimeRange.DAYS_7) stat.dayLabel else stat.dayNumber,
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = if (stat.isCurrentDay) FontWeight.Bold else FontWeight.Medium
                      ),
                      color = if (stat.isCurrentDay) primaryColor else DayFlowOnSurfaceVariant
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 5. Deep Focus Card (Stitch Reference)
      item {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("stats_deep_focus_card"),
          shape = RoundedCornerShape(16.dp),
          color = DayFlowSurfaceContainerLow,
          border = BorderStroke(1.dp, DayFlowSurfaceVariant)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp)
          ) {
            // Top row: Title + Icon
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Deep Focus",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Medium
                ),
                color = DayFlowOnSurface
              )

              Icon(
                imageVector = Icons.Filled.Psychology,
                contentDescription = null,
                tint = DayFlowSecondary,
                modifier = Modifier.size(24.dp)
              )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Center Ring Chart
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
              contentAlignment = Alignment.Center
            ) {
              val secondaryColor = DayFlowSecondary
              val secondaryTrackColor = DayFlowSecondaryFixed.copy(alpha = 0.35f)

              val focusFraction = if (statisticsData.plannedFocusMinutes > 0) {
                (statisticsData.totalFocusMinutes.toFloat() / statisticsData.plannedFocusMinutes.toFloat()).coerceIn(0f, 1f)
              } else if (statisticsData.totalFocusMinutes > 0) {
                1f
              } else {
                0f
              }

              val displaySweep = if (focusFraction > 0f) focusFraction * 360f else 0f

              Canvas(modifier = Modifier.size(120.dp)) {
                val strokeWidth = 8.dp.toPx()
                // Background track
                drawCircle(
                  color = secondaryTrackColor,
                  style = Stroke(width = strokeWidth)
                )
                // Progress arc
                if (displaySweep > 0f) {
                  drawArc(
                    color = secondaryColor,
                    startAngle = -90f,
                    sweepAngle = displaySweep,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                  )
                }
              }

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = DateUtils.formatFocusMinutes(statisticsData.totalFocusMinutes),
                  style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                  ),
                  color = DayFlowOnSurface
                )
                if (statisticsData.plannedFocusMinutes > 0) {
                  Text(
                    text = "${(focusFraction * 100).toInt()}% planned",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = DayFlowOnSurfaceVariant
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sub-metrics row: Sessions, Avg, Longest
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                color = DayFlowSurface,
                border = BorderStroke(1.dp, DayFlowSurfaceVariant)
              ) {
                Column(
                  modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "${statisticsData.focusSessionsCount}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
                    color = DayFlowOnSurface
                  )
                  Text(
                    text = "Sessions",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = DayFlowOnSurfaceVariant
                  )
                }
              }

              Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                color = DayFlowSurface,
                border = BorderStroke(1.dp, DayFlowSurfaceVariant)
              ) {
                Column(
                  modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = DateUtils.formatFocusMinutes(statisticsData.avgFocusMinutes),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
                    color = DayFlowOnSurface
                  )
                  Text(
                    text = "Avg Session",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = DayFlowOnSurfaceVariant
                  )
                }
              }

              Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                color = DayFlowSurface,
                border = BorderStroke(1.dp, DayFlowSurfaceVariant)
              ) {
                Column(
                  modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = DateUtils.formatFocusMinutes(statisticsData.longestFocusMinutes),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
                    color = DayFlowOnSurface
                  )
                  Text(
                    text = "Longest",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = DayFlowOnSurfaceVariant
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom text
            Text(
              text = if (statisticsData.totalFocusMinutes > 0) {
                "${DateUtils.formatFocusMinutes(statisticsData.totalFocusMinutes)} completed in selected range"
              } else {
                "No completed focus sessions yet"
              },
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
              color = DayFlowOnSurfaceVariant,
              modifier = Modifier.align(Alignment.CenterHorizontally)
            )
          }
        }
      }

      // 6. Consistency / Streak Card (Stitch Reference)
      item {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("stats_consistency_card"),
          shape = RoundedCornerShape(16.dp),
          color = DayFlowSurfaceContainerLow,
          border = BorderStroke(1.dp, DayFlowSurfaceVariant)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp)
          ) {
            // Top row: Title + Icon
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Consistency",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Medium
                ),
                color = DayFlowOnSurface
              )

              Icon(
                imageVector = Icons.Filled.Flare,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Large Number Display
            Row(
              modifier = Modifier.align(Alignment.CenterHorizontally),
              verticalAlignment = Alignment.Top
            ) {
              Text(
                text = "${statisticsData.currentStreak}",
                style = MaterialTheme.typography.displayMedium.copy(
                  fontSize = 60.sp,
                  fontWeight = FontWeight.Light,
                  lineHeight = 60.sp
                ),
                color = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (statisticsData.currentStreak == 1) "day" else "days",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(top = 8.dp)
              )
            }

            if (statisticsData.bestStreak > 0) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Best streak: ${statisticsData.bestStreak} ${if (statisticsData.bestStreak == 1) "day" else "days"}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = DayFlowOnSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Inner Bottom Row: Current Streak + Indicator Dots
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = DayFlowSurface,
              border = BorderStroke(1.dp, DayFlowSurfaceVariant),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Recent Activity (5 days)",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                  color = DayFlowOnSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  if (statisticsData.recentStreakDays.isNotEmpty()) {
                    val dotColor = MaterialTheme.colorScheme.primary
                    statisticsData.recentStreakDays.forEach { isCompletedDay ->
                      Box(
                        modifier = Modifier
                          .size(10.dp)
                          .clip(CircleShape)
                          .background(if (isCompletedDay) dotColor else DayFlowSurfaceVariant)
                      )
                    }
                  } else {
                    repeat(5) {
                      Box(
                        modifier = Modifier
                          .size(10.dp)
                          .clip(CircleShape)
                          .background(DayFlowSurfaceVariant)
                      )
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Manual Streak Adjustments (Pill buttons)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedButton(
                onClick = { pendingStreakAction = StreakConfirmAction.ADD_DAY },
                shape = CircleShape,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("stats_streak_add_day")
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                  Text("+1 Day", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
              }

              OutlinedButton(
                onClick = { pendingStreakAction = StreakConfirmAction.REMOVE_DAY },
                enabled = statisticsData.currentStreak > 0,
                shape = CircleShape,
                border = BorderStroke(1.dp, DayFlowSurfaceVariant),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("stats_streak_remove_day")
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(imageVector = Icons.Filled.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                  Text("-1 Day", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
              }

              OutlinedButton(
                onClick = { pendingStreakAction = StreakConfirmAction.RESET },
                enabled = statisticsData.currentStreak > 0,
                shape = CircleShape,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("stats_streak_reset")
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(imageVector = Icons.Filled.Replay, contentDescription = null, modifier = Modifier.size(16.dp))
                  Text("Reset", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
              }
            }
          }
        }
      }

      // 7. Category Breakdown Section
      if (statisticsData.categoryStats.isNotEmpty()) {
        item {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = DayFlowSurfaceContainerLow,
            border = BorderStroke(1.dp, DayFlowSurfaceVariant),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("stats_category_breakdown")
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
              verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Category Distribution",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                  ),
                  color = DayFlowOnSurface
                )

                Icon(
                  imageVector = Icons.Filled.PieChart,
                  contentDescription = null,
                  tint = DayFlowSecondary,
                  modifier = Modifier.size(20.dp)
                )
              }

              statisticsData.categoryStats.forEach { catStat ->
                val categoryColor = Color(catStat.category.colorHex)
                Column(
                  modifier = Modifier.fillMaxWidth(),
                  verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Box(
                        modifier = Modifier
                          .size(8.dp)
                          .clip(CircleShape)
                          .background(categoryColor)
                      )
                      Text(
                        text = catStat.category.displayName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                          fontSize = 14.sp,
                          fontWeight = FontWeight.Medium
                        ),
                        color = DayFlowOnSurface
                      )
                    }

                    Text(
                      text = "${catStat.completedCount}/${catStat.totalCount} done (${catStat.percentage}%)",
                      style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                      color = DayFlowOnSurfaceVariant
                    )
                  }

                  // Progress Bar
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(6.dp)
                      .clip(RoundedCornerShape(3.dp))
                      .background(DayFlowSurfaceVariant.copy(alpha = 0.5f))
                  ) {
                    val progressFraction = if (catStat.totalCount > 0) {
                      catStat.completedCount.toFloat() / catStat.totalCount.toFloat()
                    } else {
                      0f
                    }
                    Box(
                      modifier = Modifier
                        .fillMaxWidth(progressFraction.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(categoryColor)
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 8. Weekly Insights Section (Stitch Reference)
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "Insights",
            style = MaterialTheme.typography.titleMedium.copy(
              fontSize = 18.sp,
              fontWeight = FontWeight.Medium
            ),
            color = DayFlowOnSurface,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Insight Card 1 (Lavender Wash)
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0x14A5A5D4),
            border = BorderStroke(1.dp, Color(0x28A5A5D4)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.Top,
              horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(DayFlowSecondary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.AutoAwesome,
                  contentDescription = null,
                  tint = DayFlowSecondary,
                  modifier = Modifier.size(18.dp)
                )
              }

              val insightText = buildAnnotatedString {
                append("You've logged ")
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = DayFlowOnSurface)) {
                  append(DateUtils.formatFocusMinutes(statisticsData.totalFocusMinutes))
                }
                append(" of dedicated focus time across ")
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = DayFlowOnSurface)) {
                  append("${statisticsData.tasksCompleted} completed tasks")
                }
                append(". Keep maintaining your momentum during peak morning windows.")
              }

              Text(
                text = insightText,
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 14.sp,
                  lineHeight = 20.sp
                ),
                color = DayFlowOnSurface
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Insight Card 2 (Surface Container Low)
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = DayFlowSurfaceContainerLow,
            border = BorderStroke(1.dp, DayFlowSurfaceVariant),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.Top,
              horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.TrendingUp,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp)
                )
              }

              Text(
                text = if (statisticsData.completionRate > 0) {
                  "You've maintained a ${statisticsData.completionRate}% completion rate over the selected ${selectedRange.label.lowercase()}."
                } else {
                  "Complete scheduled tasks to build higher consistency and boost your overall completion score."
                },
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 14.sp,
                  lineHeight = 20.sp
                ),
                color = DayFlowOnSurface
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(72.dp))
    }
  }

  if (pendingStreakAction != null) {
    val action = pendingStreakAction!!
    val title = when (action) {
      StreakConfirmAction.ADD_DAY -> "Add Day to Streak"
      StreakConfirmAction.REMOVE_DAY -> "Remove Day from Streak"
      StreakConfirmAction.RESET -> "Reset Streak"
    }
    val message = when (action) {
      StreakConfirmAction.ADD_DAY -> "Extend your consistency streak by 1 day?"
      StreakConfirmAction.REMOVE_DAY -> "Reduce your consistency streak by 1 day?"
      StreakConfirmAction.RESET -> "Reset your active streak back to 0 days? Your historical best streak will be preserved."
    }
    val confirmText = when (action) {
      StreakConfirmAction.RESET -> "Reset"
      else -> "Confirm"
    }
    val isDestructive = action == StreakConfirmAction.RESET || action == StreakConfirmAction.REMOVE_DAY

    AlertDialog(
      onDismissRequest = { pendingStreakAction = null },
      title = {
        Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = DayFlowOnSurface)
      },
      text = {
        Text(text = message, style = MaterialTheme.typography.bodyMedium, color = DayFlowOnSurfaceVariant)
      },
      confirmButton = {
        Button(
          onClick = {
            when (action) {
              StreakConfirmAction.ADD_DAY -> onAddStreakDay()
              StreakConfirmAction.REMOVE_DAY -> onRemoveStreakDay()
              StreakConfirmAction.RESET -> onResetStreak()
            }
            pendingStreakAction = null
          },
          colors = if (isDestructive) {
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = DayFlowOnPrimary)
          } else {
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = DayFlowOnPrimary)
          }
        ) {
          Text(confirmText)
        }
      },
      dismissButton = {
        TextButton(onClick = { pendingStreakAction = null }) {
          Text("Cancel", color = DayFlowOnSurfaceVariant)
        }
      },
      containerColor = DayFlowSurfaceContainerLowest,
      shape = RoundedCornerShape(16.dp)
    )
  }
}

@Composable
private fun MetricTile(
  title: String,
  value: String,
  subtitle: String,
  modifier: Modifier = Modifier,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  tint: Color
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = DayFlowSurfaceContainerLow,
    border = BorderStroke(1.dp, DayFlowSurfaceVariant),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
          color = DayFlowOnSurfaceVariant
        )
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = tint,
          modifier = Modifier.size(14.dp)
        )
      }

      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium.copy(
          fontSize = 18.sp,
          fontWeight = FontWeight.SemiBold
        ),
        color = DayFlowOnSurface
      )

      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
        color = DayFlowOnSurfaceVariant,
        maxLines = 1
      )
    }
  }
}
