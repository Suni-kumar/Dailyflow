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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.CoachActionType
import com.example.data.ai.DayFlowAiService
import com.example.model.CoachInsight
import com.example.model.DailyProgressSummary
import com.example.model.InsightType
import com.example.ui.theme.DayFlowBackground
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOnPrimary
import com.example.ui.theme.DayFlowOnSurface
import com.example.ui.theme.DayFlowOnSurfaceVariant
import com.example.ui.theme.DayFlowOutlineVariant
import com.example.ui.theme.DayFlowSecondary
import com.example.ui.theme.DayFlowSecondaryFixed
import com.example.ui.theme.DayFlowSurface
import com.example.ui.theme.DayFlowSurfaceContainerLow
import com.example.ui.theme.DayFlowTertiary
import com.example.ui.theme.DayFlowTertiaryContainer

@Composable
fun AiCoachScreen(
  insights: List<CoachInsight> = emptyList(),
  summary: DailyProgressSummary = DailyProgressSummary(0, 0, 0, 0, 0, 0),
  chatMessages: List<Pair<String, Boolean>> = emptyList(),
  isThinking: Boolean = false,
  onSendPrompt: (String) -> Unit = {},
  onTriggerAction: (CoachActionType) -> Unit = {}
) {
  var userInput by remember { mutableStateOf("") }
  val isGeminiConfigured = remember { DayFlowAiService.isGeminiConfigured() }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(DayFlowBackground)
      .testTag("ai_coach_screen")
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Header Section
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // AI Insights Badge
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Filled.AutoAwesome,
              contentDescription = null,
              tint = DayFlowSecondary,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = if (isGeminiConfigured) "GEMINI COACH" else "LOCAL AI COACH",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp
              ),
              color = DayFlowSecondary
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Your Mindful Coach",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontSize = 28.sp,
              fontWeight = FontWeight.Normal,
              letterSpacing = (-0.3).sp
            ),
            color = DayFlowOnSurface
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Personalized reflections grounded in your daily schedule, habits, and intentional flow.",
            style = MaterialTheme.typography.bodyMedium.copy(
              fontSize = 15.sp,
              lineHeight = 22.sp
            ),
            color = DayFlowOnSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
          )
        }
      }

      // 2. Quick Action Cards (Daily Briefing, Day Review, Goal Guidance)
      item {
        Text(
          text = "GUIDED REFLECTIONS",
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp
          ),
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
        )
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          QuickActionCard(
            modifier = Modifier.weight(1f),
            title = "Daily Briefing",
            subtitle = "Plan morning flow",
            icon = Icons.Filled.SelfImprovement,
            tint = MaterialTheme.colorScheme.primary,
            onClick = { onTriggerAction(CoachActionType.DAILY_BRIEFING) },
            testTag = "coach_action_briefing"
          )

          QuickActionCard(
            modifier = Modifier.weight(1f),
            title = "Day Review",
            subtitle = "Evening reflection",
            icon = Icons.Filled.Bedtime,
            tint = MaterialTheme.colorScheme.secondary,
            onClick = { onTriggerAction(CoachActionType.DAY_REVIEW) },
            testTag = "coach_action_review"
          )

          QuickActionCard(
            modifier = Modifier.weight(1f),
            title = "Goals",
            subtitle = "Momentum check",
            icon = Icons.Filled.TrackChanges,
            tint = DayFlowTertiary,
            onClick = { onTriggerAction(CoachActionType.GOAL_GUIDANCE) },
            testTag = "coach_action_goals"
          )
        }
      }

      // 3. Dynamic Insight Cards
      if (insights.isNotEmpty()) {
        item {
          Text(
            text = "DAILY OBSERVATIONS",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 1.2.sp
            ),
            color = DayFlowSecondary,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
          )
        }

        items(insights, key = { it.id }) { insight ->
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("ai_insight_${insight.id}"),
            shape = RoundedCornerShape(16.dp),
            color = DayFlowSurface,
            border = BorderStroke(1.dp, DayFlowOutlineVariant.copy(alpha = 0.5f))
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
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
                  Icon(
                    imageVector = when (insight.type) {
                      InsightType.HABIT_ALERT -> Icons.Filled.CheckCircle
                      InsightType.PRODUCTIVITY_TIP -> Icons.Filled.Lightbulb
                      InsightType.MOTIVATION -> Icons.Filled.SelfImprovement
                      InsightType.ADVICE -> Icons.Filled.AutoAwesome
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                  )
                  Text(
                    text = insight.type.name,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 11.sp,
                      fontWeight = FontWeight.SemiBold,
                      letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                  )
                }

                Text(
                  text = insight.timestamp,
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                  color = DayFlowOnSurfaceVariant
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = insight.title,
                style = MaterialTheme.typography.titleSmall.copy(
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Medium
                ),
                color = DayFlowOnSurface
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = insight.description,
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 14.sp,
                  lineHeight = 20.sp
                ),
                color = DayFlowOnSurfaceVariant
              )
            }
          }
        }
      }

      // 4. Conversation Stream
      if (chatMessages.isNotEmpty()) {
        item {
          Text(
            text = "COACH REFLECTIONS",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 1.2.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
          )
        }

        items(chatMessages) { (text, isUser) ->
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (isUser) MaterialTheme.colorScheme.primaryContainer else DayFlowSurfaceContainerLow,
            border = BorderStroke(1.dp, DayFlowCardBorder),
            modifier = Modifier
              .fillMaxWidth()
              .padding(start = if (isUser) 32.dp else 0.dp, end = if (isUser) 0.dp else 32.dp)
          ) {
            Text(
              text = text,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 20.sp
              ),
              color = DayFlowOnSurface,
              modifier = Modifier.padding(16.dp)
            )
          }
        }
      }

      // Thinking Indicator
      if (isThinking) {
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(20.dp),
              color = MaterialTheme.colorScheme.primary,
              strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Reflecting...",
              style = MaterialTheme.typography.bodySmall,
              color = DayFlowOnSurfaceVariant
            )
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(130.dp))
      }
    }

    // 5. Floating Bottom Integrated Chat Input Area
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(horizontal = 24.dp, vertical = 72.dp)
        .fillMaxWidth()
    ) {
      Surface(
        shape = CircleShape,
        color = DayFlowSurface.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, DayFlowOutlineVariant),
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Outlined.Psychology,
            contentDescription = null,
            tint = DayFlowOnSurfaceVariant,
            modifier = Modifier.size(20.dp)
          )

          Spacer(modifier = Modifier.width(8.dp))

          // Input Text
          Box(
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 4.dp),
            contentAlignment = Alignment.CenterStart
          ) {
            if (userInput.isEmpty()) {
              Text(
                text = "Ask your coach anything...",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = DayFlowOnSurfaceVariant.copy(alpha = 0.6f)
              )
            }
            val primaryColor = MaterialTheme.colorScheme.primary
            BasicTextField(
              value = userInput,
              onValueChange = { userInput = it },
              textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = DayFlowOnSurface
              ),
              cursorBrush = SolidColor(primaryColor),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("ai_prompt_input"),
              singleLine = true
            )
          }

          // Send Button
          val primaryColor = MaterialTheme.colorScheme.primary
          Surface(
            shape = CircleShape,
            color = if (userInput.isNotBlank()) primaryColor else primaryColor.copy(alpha = 0.5f),
            modifier = Modifier
              .size(36.dp)
              .clickable(enabled = userInput.isNotBlank() && !isThinking) {
                val prompt = userInput.trim()
                if (prompt.isNotBlank()) {
                  onSendPrompt(prompt)
                  userInput = ""
                }
              }
              .testTag("ai_send_button")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Filled.ArrowUpward,
                contentDescription = "Send",
                tint = DayFlowOnPrimary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun QuickActionCard(
  modifier: Modifier = Modifier,
  title: String,
  subtitle: String,
  icon: ImageVector,
  tint: Color,
  onClick: () -> Unit,
  testTag: String
) {
  Surface(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .clickable { onClick() }
      .testTag(testTag),
    shape = RoundedCornerShape(14.dp),
    color = DayFlowSurface,
    border = BorderStroke(1.dp, DayFlowOutlineVariant.copy(alpha = 0.5f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(tint.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = tint,
          modifier = Modifier.size(18.dp)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold
        ),
        color = DayFlowOnSurface,
        maxLines = 1
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
        color = DayFlowOnSurfaceVariant,
        maxLines = 1
      )
    }
  }
}
