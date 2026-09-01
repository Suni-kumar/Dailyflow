package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyProgressSummary
import com.example.model.TaskItem
import com.example.ui.theme.DayFlowBackground
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOnPrimary
import com.example.ui.theme.DayFlowOnSurface
import com.example.ui.theme.DayFlowOnSurfaceVariant
import com.example.ui.theme.DayFlowOutlineVariant
import com.example.ui.theme.DayFlowPrimary
import com.example.ui.theme.DayFlowPrimaryContainer
import com.example.ui.theme.DayFlowPrimaryFixed
import com.example.ui.theme.DayFlowSecondary
import com.example.ui.theme.DayFlowSecondaryContainer
import com.example.ui.theme.DayFlowSecondaryFixed
import com.example.ui.theme.DayFlowSurface
import com.example.ui.theme.DayFlowSurfaceContainerLow
import com.example.ui.theme.DayFlowSurfaceContainerLowest
import com.example.ui.theme.DayFlowSurfaceVariant
import com.example.ui.theme.DayFlowTertiary
import com.example.ui.theme.DayFlowTertiaryContainer

data class DailyBarData(
  val day: String,
  val heightFraction: Float,
  val barColor: Color,
  val isCurrentDay: Boolean = false,
  val tooltipValue: String = ""
)

@Composable
fun StatisticsScreen(
  summary: DailyProgressSummary = DailyProgressSummary(0, 0, 0, 0, 0, 0),
  tasks: List<TaskItem> = emptyList()
) {
  val weekBars = listOf(
    DailyBarData("Mon", 0.40f, DayFlowSecondaryFixed.copy(alpha = 0.6f), tooltipValue = "4h"),
    DailyBarData("Tue", 0.75f, DayFlowPrimaryFixed.copy(alpha = 0.9f), tooltipValue = "7.5h"),
    DailyBarData("Wed", 0.55f, DayFlowSecondaryFixed.copy(alpha = 0.8f), tooltipValue = "5.5h"),
    DailyBarData("Thu", 0.90f, DayFlowPrimary, isCurrentDay = true, tooltipValue = "9h"),
    DailyBarData("Fri", 0.30f, DayFlowSurfaceVariant, tooltipValue = "3h"),
    DailyBarData("Sat", 0.15f, DayFlowSurfaceVariant.copy(alpha = 0.7f), tooltipValue = "1.5h"),
    DailyBarData("Sun", 0.05f, DayFlowSurfaceVariant.copy(alpha = 0.4f), tooltipValue = "0.5h")
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(DayFlowBackground)
      .testTag("statistics_screen"),
    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(28.dp)
  ) {
    // 1. Weekly Activity Trends Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Text(
          text = "Activity Trends",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = (-0.3).sp
          ),
          color = DayFlowOnSurface
        )

        Text(
          text = "THIS WEEK",
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
          ),
          color = DayFlowOnSurfaceVariant
        )
      }
    }

    // 2. Weekly Bar Chart Card (Stitch Reference)
    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .height(240.dp)
          .testTag("stats_weekly_chart"),
        shape = RoundedCornerShape(16.dp),
        color = DayFlowSurfaceContainerLow,
        border = BorderStroke(1.dp, DayFlowSurfaceVariant)
      ) {
        Row(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          weekBars.forEach { bar ->
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Bottom,
              modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
            ) {
              // Bar Column
              Box(
                modifier = Modifier
                  .width(28.dp)
                  .weight(1f, fill = false)
                  .fillMaxHeight(bar.heightFraction)
                  .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                  .background(bar.barColor)
              )

              Spacer(modifier = Modifier.height(12.dp))

              // Day Label
              Text(
                text = bar.day,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 12.sp,
                  fontWeight = if (bar.isCurrentDay) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (bar.isCurrentDay) DayFlowPrimary else DayFlowOnSurfaceVariant
              )
            }
          }
        }
      }
    }

    // 3. Metric 1: Deep Focus Card
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

            Canvas(modifier = Modifier.size(120.dp)) {
              val strokeWidth = 8.dp.toPx()
              // Background track
              drawCircle(
                color = secondaryTrackColor,
                style = Stroke(width = strokeWidth)
              )
              // Progress arc (75%)
              drawArc(
                color = secondaryColor,
                startAngle = -90f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
              )
            }

            Text(
              text = "75%",
              style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Light
              ),
              color = DayFlowOnSurface
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Bottom text
          Text(
            text = "+12% from last week",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
            color = DayFlowOnSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
          )
        }
      }
    }

    // 4. Metric 2: Consistency Card
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
              tint = DayFlowPrimary,
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Large Number Display
          Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Top
          ) {
            Text(
              text = "14",
              style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 64.sp,
                fontWeight = FontWeight.Light,
                lineHeight = 64.sp
              ),
              color = DayFlowPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "days",
              style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
              ),
              color = DayFlowPrimaryContainer,
              modifier = Modifier.padding(top = 4.dp)
            )
          }

          Spacer(modifier = Modifier.height(24.dp))

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
                text = "Current Streak",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                color = DayFlowOnSurfaceVariant
              )

              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(4) {
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .clip(CircleShape)
                      .background(DayFlowPrimary)
                  )
                }
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(DayFlowSurfaceVariant)
                )
              }
            }
          }
        }
      }
    }

    // 5. Weekly Insights Section
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Weekly Insights",
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
              append("Your focus time peaks between ")
              withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = DayFlowOnSurface)) {
                append("9:00 AM")
              }
              append(" and ")
              withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = DayFlowOnSurface)) {
                append("11:30 AM")
              }
              append(". Try scheduling complex tasks during this window to maximize deep work.")
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
                .background(DayFlowPrimary.copy(alpha = 0.12f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.TrendingUp,
                contentDescription = null,
                tint = DayFlowPrimary,
                modifier = Modifier.size(18.dp)
              )
            }

            Text(
              text = "You've maintained your habit consistency 20% better this week compared to last month.",
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

    item {
      Spacer(modifier = Modifier.height(72.dp))
    }
  }
}
