package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.CoachActionType
import com.example.model.AiChatMessage
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
import com.example.ui.theme.DayFlowSurface
import com.example.ui.theme.DayFlowSurfaceContainerLow
import com.example.ui.theme.DayFlowTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiCoachScreen(
  insights: List<CoachInsight> = emptyList(),
  summary: DailyProgressSummary = DailyProgressSummary(0, 0, 0, 0, 0, 0),
  chatMessages: List<AiChatMessage> = emptyList(),
  chatSessions: List<com.example.model.AiChatSession> = emptyList(),
  isThinking: Boolean = false,
  isGeminiConfigured: Boolean = false,
  isGeminiConnected: Boolean = false,
  onSendPrompt: (String) -> Unit = {},
  onTriggerAction: (CoachActionType) -> Unit = {},
  onStopGeneration: () -> Unit = {},
  onRegenerate: () -> Unit = {},
  onRetry: () -> Unit = {},
  onClearChat: () -> Unit = {},
  onNewChat: () -> Unit = {},
  onLoadSession: (String) -> Unit = {},
  onDeleteSession: (String) -> Unit = {},
  onOpenSettings: () -> Unit = {}
) {
  var userInput by remember { mutableStateOf("") }
  val context = LocalContext.current
  val listState = rememberLazyListState()
  var showRecentChats by remember { mutableStateOf(false) }

  // Auto scroll down when new message or chunk arrives
  LaunchedEffect(chatMessages.size, chatMessages.lastOrNull()?.text?.length) {
    if (chatMessages.isNotEmpty()) {
      listState.animateScrollToItem(listState.layoutInfo.totalItemsCount.coerceAtLeast(1) - 1)
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(DayFlowBackground)
      .testTag("ai_coach_screen")
  ) {
    LazyColumn(
      state = listState,
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
          // AI Mode Badge
          val badgeColor = when {
            isGeminiConnected -> DayFlowSecondary
            isGeminiConfigured -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.primary
          }
          val badgeText = when {
            isGeminiConnected -> "GEMINI 3.5 FLASH ACTIVE"
            isGeminiConfigured -> "GEMINI CONFIGURED (UNVERIFIED)"
            else -> "MINDFUL LOCAL COACH"
          }

          Surface(
            shape = CircleShape,
            color = badgeColor.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.3f)),
            modifier = Modifier
              .clickable { onOpenSettings() }
              .testTag("ai_coach_status_badge")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = badgeColor,
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = badgeText,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  letterSpacing = 1.1.sp
                ),
                color = badgeColor
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Your Mindful Coach",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontSize = 28.sp,
              fontWeight = FontWeight.Normal,
              letterSpacing = (-0.3).sp
            ),
            color = DayFlowOnSurface
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Reflections grounded in your daily schedule, habits, and momentum.",
            style = MaterialTheme.typography.bodyMedium.copy(
              fontSize = 14.sp,
              lineHeight = 20.sp
            ),
            color = DayFlowOnSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
          )
        }
      }

      // 2. Guided Reflection Action Cards
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "GUIDED REFLECTIONS",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 1.2.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp)
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Row(
              modifier = Modifier
                .clip(CircleShape)
                .clickable { showRecentChats = true }
                .padding(horizontal = 8.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Recent Chats",
                tint = DayFlowOnSurfaceVariant,
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = "Recent",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = DayFlowOnSurfaceVariant
              )
            }
            if (chatMessages.isNotEmpty()) {
              Row(
                modifier = Modifier
                  .clip(CircleShape)
                  .clickable { onNewChat() }
                  .padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = "New Chat",
                  tint = DayFlowOnSurfaceVariant,
                  modifier = Modifier.size(14.dp)
                )
                Text(
                  text = "New Chat",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                  color = DayFlowOnSurfaceVariant
                )
              }
            }
          }
        }
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

      // Quick Inquiries when no active conversation
      if (chatMessages.isEmpty()) {
        item {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "QUICK INQUIRIES",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.1.sp
              ),
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(start = 4.dp)
            )

            val suggestions = listOf(
              "What should I prioritize today?",
              "How is my goal progressing?",
              "Meri consistency kaisi hai?",
              "What tasks are still pending?"
            )

            suggestions.forEach { suggestion ->
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = DayFlowSurfaceContainerLow,
                border = BorderStroke(1.dp, DayFlowCardBorder),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { onSendPrompt(suggestion) }
                  .testTag("ai_suggestion_chip_${suggestion.take(8).replace(" ", "_").lowercase()}")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = suggestion,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
                    color = DayFlowOnSurface
                  )
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = DayFlowOnSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                  )
                }
              }
            }
          }
        }
      }

      // 3. Conversation Stream
      if (chatMessages.isNotEmpty()) {
        item {
          Text(
            text = "CONVERSATION",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 1.2.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
          )
        }

        items(chatMessages, key = { it.id }) { message ->
          val isLastModelMessage = !message.isUser && message == chatMessages.lastOrNull { !it.isUser }

          Column(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("ai_chat_bubble_${message.id}"),
            horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
          ) {
            Surface(
              shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
              ),
              color = if (message.isUser) {
                MaterialTheme.colorScheme.primaryContainer
              } else if (message.isError) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
              } else {
                DayFlowSurfaceContainerLow
              },
              border = BorderStroke(
                1.dp,
                if (message.isError) MaterialTheme.colorScheme.error.copy(alpha = 0.3f) else DayFlowCardBorder
              ),
              modifier = Modifier
                .fillMaxWidth(0.92f)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                if (message.isError) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.ErrorOutline,
                      contentDescription = "Error",
                      tint = MaterialTheme.colorScheme.error,
                      modifier = Modifier.size(16.dp)
                    )
                    Text(
                      text = "Generation Error",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                      color = MaterialTheme.colorScheme.error
                    )
                  }
                  Spacer(modifier = Modifier.height(6.dp))
                }

                if (message.text.isNotEmpty()) {
                  Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontSize = 14.5.sp,
                      lineHeight = 21.sp
                    ),
                    color = DayFlowOnSurface
                  )
                }

                if (message.isStreaming) {
                  StreamingCursor()
                }
              }
            }

            // Message action tools for Model Responses
            if (!message.isUser && !message.isStreaming && message.text.isNotEmpty()) {
              Row(
                modifier = Modifier
                  .padding(top = 4.dp, start = 4.dp)
                  .fillMaxWidth(0.92f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                // Copy Button
                Row(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable {
                      val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                      val clip = ClipData.newPlainText("DayFlow AI", message.text)
                      clipboard.setPrimaryClip(clip)
                      Toast.makeText(context, "Copied reflection to clipboard", Toast.LENGTH_SHORT).show()
                    }
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = DayFlowOnSurfaceVariant,
                    modifier = Modifier.size(13.dp)
                  )
                  Text(
                    text = "Copy",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = DayFlowOnSurfaceVariant
                  )
                }

                // Regenerate button if it's the last response
                if (isLastModelMessage) {
                  Row(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .clickable { onRegenerate() }
                      .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Refresh,
                      contentDescription = "Regenerate",
                      tint = DayFlowOnSurfaceVariant,
                      modifier = Modifier.size(13.dp)
                    )
                    Text(
                      text = "Regenerate",
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                      color = DayFlowOnSurfaceVariant
                    )
                  }
                }
              }
            }

            // Error Retry
            if (message.isError && isLastModelMessage) {
              Row(
                modifier = Modifier
                  .padding(top = 4.dp, start = 4.dp)
                  .clickable { onRetry() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Replay,
                  contentDescription = "Retry",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(14.dp)
                )
                Text(
                  text = "Retry reflection",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        }
      }

      // 4. Dynamic Observations Section
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

      item {
        Spacer(modifier = Modifier.height(140.dp))
      }
    }

    // 5. Floating Bottom Integrated Chat Input Area
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(horizontal = 20.dp, vertical = 72.dp)
        .fillMaxWidth()
    ) {
      Surface(
        shape = RoundedCornerShape(28.dp),
        color = DayFlowSurface.copy(alpha = 0.98f),
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
                text = if (isThinking) "Coach is generating..." else "Ask your coach anything...",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = DayFlowOnSurfaceVariant.copy(alpha = 0.6f)
              )
            }
            val primaryColor = MaterialTheme.colorScheme.primary
            BasicTextField(
              value = userInput,
              onValueChange = { userInput = it },
              enabled = !isThinking,
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

          // Action Button: Stop or Send
          if (isThinking) {
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
              modifier = Modifier
                .size(36.dp)
                .clickable { onStopGeneration() }
                .testTag("ai_stop_button")
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Filled.Stop,
                  contentDescription = "Stop",
                  tint = MaterialTheme.colorScheme.error,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          } else {
            val primaryColor = MaterialTheme.colorScheme.primary
            Surface(
              shape = CircleShape,
              color = if (userInput.isNotBlank()) primaryColor else primaryColor.copy(alpha = 0.5f),
              modifier = Modifier
                .size(36.dp)
                .clickable(enabled = userInput.isNotBlank()) {
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

  if (showRecentChats) {
    ModalBottomSheet(
      onDismissRequest = { showRecentChats = false },
      sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
      containerColor = DayFlowSurface
    ) {
      ChatHistorySheet(
        sessions = chatSessions,
        onLoadSession = {
          onLoadSession(it)
          showRecentChats = false
        },
        onDeleteSession = onDeleteSession
      )
    }
  }
}

@Composable
fun ChatHistorySheet(
  sessions: List<com.example.model.AiChatSession>,
  onLoadSession: (String) -> Unit,
  onDeleteSession: (String) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp)
      .padding(bottom = 32.dp)
  ) {
    Text(
      text = "Recent Chats",
      style = MaterialTheme.typography.titleMedium,
      color = DayFlowOnSurface
    )
    Spacer(modifier = Modifier.height(16.dp))

    if (sessions.isEmpty()) {
      Text(
        text = "Your conversations will appear here.",
        style = MaterialTheme.typography.bodyMedium,
        color = DayFlowOnSurfaceVariant
      )
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(sessions, key = { it.id }) { session ->
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = DayFlowSurfaceContainerLow,
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onLoadSession(session.id) }
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = session.title.takeIf { it.isNotBlank() } ?: "Untitled Chat",
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                  color = DayFlowOnSurface
                )
              }
              IconButton(onClick = { onDeleteSession(session.id) }) {
                Icon(
                  imageVector = Icons.Default.Delete,
                  contentDescription = "Delete Chat",
                  tint = DayFlowOnSurfaceVariant,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}


@Composable
private fun StreamingCursor() {
  val infiniteTransition = rememberInfiniteTransition(label = "streaming_cursor")
  val alpha by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(500),
      repeatMode = RepeatMode.Reverse
    ),
    label = "cursor_alpha"
  )

  Row(
    modifier = Modifier.padding(top = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
    )
    Text(
      text = "Thinking...",
      style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
      color = DayFlowOnSurfaceVariant.copy(alpha = alpha)
    )
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
