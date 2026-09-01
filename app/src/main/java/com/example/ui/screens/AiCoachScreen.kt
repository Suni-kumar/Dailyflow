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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.model.CoachInsight
import com.example.model.DailyProgressSummary
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

private data class CoachInsightCardData(
  val id: String,
  val icon: ImageVector,
  val iconBgColor: Color,
  val iconColor: Color,
  val category: String,
  val categoryColor: Color,
  val title: String,
  val description: String,
  val actionButtonText: String? = null,
  val bgWashAlpha: Float = 0.08f
)

@Composable
fun AiCoachScreen(
  insights: List<CoachInsight> = emptyList(),
  summary: DailyProgressSummary = DailyProgressSummary(0, 0, 0, 0, 0, 0),
  onSendPrompt: (String) -> Unit = {}
) {
  var userInput by remember { mutableStateOf("") }
  val messages = remember { mutableStateListOf<Pair<String, Boolean>>() } // text, isUser

  val coachCards = remember {
    listOf(
      CoachInsightCardData(
        id = "1",
        icon = Icons.Filled.SelfImprovement,
        iconBgColor = DayFlowSecondaryFixed.copy(alpha = 0.4f),
        iconColor = DayFlowSecondary,
        category = "CONSISTENCY",
        categoryColor = DayFlowSecondary,
        title = "Meditation Streak",
        description = "You've been remarkably consistent with your morning meditation this week. This foundation is clearly supporting your reported focus levels. Keep it up.",
        bgWashAlpha = 0.08f
      ),
      CoachInsightCardData(
        id = "2",
        icon = Icons.Filled.Bedtime,
        iconBgColor = DayFlowPrimaryFixed.copy(alpha = 0.5f),
        iconColor = DayFlowPrimary,
        category = "OBSERVATION",
        categoryColor = DayFlowPrimary,
        title = "Rest Quality Drop",
        description = "I noticed a slight dip in your sleep quality correlating with late-night screen time. Consider setting a digital sundown at 9 PM tonight.",
        bgWashAlpha = 0.05f
      ),
      CoachInsightCardData(
        id = "3",
        icon = Icons.Filled.DirectionsWalk,
        iconBgColor = DayFlowTertiaryContainer.copy(alpha = 0.35f),
        iconColor = DayFlowTertiary,
        category = "SUGGESTION",
        categoryColor = DayFlowTertiary,
        title = "Midday Movement",
        description = "Your afternoon focus tends to wane around 2 PM. Adding a brief 10-minute walk before this window might help reset your cognitive load.",
        actionButtonText = "SCHEDULE WALK",
        bgWashAlpha = 0.05f
      )
    )
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(DayFlowBackground)
      .testTag("ai_coach_screen")
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp)
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
              text = "AI INSIGHTS",
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
            text = "Personalized reflections based on your daily data to help you maintain a calm, intentional flow.",
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

      // 2. Insight Bento Cards
      items(coachCards, key = { it.id }) { card ->
        CoachInsightCard(card = card)
      }

      // Dynamically sent messages (Simulated conversation)
      items(messages) { (text, isUser) ->
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = if (isUser) DayFlowPrimaryContainer else DayFlowSurfaceContainerLow,
          border = BorderStroke(1.dp, DayFlowCardBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = DayFlowOnSurface,
            modifier = Modifier.padding(16.dp)
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(130.dp))
      }
    }

    // 3. Floating Bottom Integrated Chat Input Area (Stitch Reference)
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
            .padding(horizontal = 6.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Mic Button
          IconButton(
            onClick = {},
            modifier = Modifier.size(38.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Mic,
              contentDescription = "Voice Input",
              tint = DayFlowOnSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
          }

          // Input Text
          Box(
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
          ) {
            if (userInput.isEmpty()) {
              Text(
                text = "Ask your coach anything...",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = DayFlowOnSurfaceVariant.copy(alpha = 0.6f)
              )
            }
            BasicTextField(
              value = userInput,
              onValueChange = { userInput = it },
              textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = DayFlowOnSurface
              ),
              cursorBrush = SolidColor(DayFlowPrimary),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("ai_prompt_input"),
              singleLine = true
            )
          }

          // Send / Arrow Upward Button
          Surface(
            shape = CircleShape,
            color = DayFlowPrimary,
            modifier = Modifier
              .size(38.dp)
              .clickable {
                if (userInput.isNotBlank()) {
                  val prompt = userInput.trim()
                  messages.add(prompt to true)
                  onSendPrompt(prompt)
                  userInput = ""
                  messages.add("I'm looking into your schedule and focus patterns. Take a breath and prioritize your key objective today." to false)
                }
              }
              .testTag("ai_send_button")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Filled.ArrowUpward,
                contentDescription = "Send",
                tint = DayFlowOnPrimary,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CoachInsightCard(card: CoachInsightCardData) {
  val lavenderWash = Color(0xFFA5A5D4).copy(alpha = card.bgWashAlpha)

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("ai_insight_${card.id}"),
    shape = RoundedCornerShape(16.dp),
    color = lavenderWash,
    border = BorderStroke(1.dp, DayFlowOutlineVariant.copy(alpha = 0.6f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      // Top Row: Icon Circle + Category Label
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(card.iconBgColor),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = card.icon,
            contentDescription = null,
            tint = card.iconColor,
            modifier = Modifier.size(22.dp)
          )
        }

        Text(
          text = card.category,
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
          ),
          color = card.categoryColor
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Title
      Text(
        text = card.title,
        style = MaterialTheme.typography.titleMedium.copy(
          fontSize = 18.sp,
          fontWeight = FontWeight.Medium
        ),
        color = DayFlowOnSurface
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Description
      Text(
        text = card.description,
        style = MaterialTheme.typography.bodySmall.copy(
          fontSize = 14.sp,
          lineHeight = 20.sp
        ),
        color = DayFlowOnSurfaceVariant
      )

      // Optional Schedule Walk button
      if (card.actionButtonText != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = card.actionButtonText,
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
          ),
          color = DayFlowPrimary,
          modifier = Modifier
            .clickable { }
            .padding(bottom = 2.dp)
        )
        Box(
          modifier = Modifier
            .width(100.dp)
            .height(1.dp)
            .background(DayFlowPrimary.copy(alpha = 0.4f))
        )
      }
    }
  }
}
