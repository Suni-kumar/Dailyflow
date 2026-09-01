package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HabitItem
import com.example.ui.theme.DayFlowBackground
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOnPrimary
import com.example.ui.theme.DayFlowOnSurface
import com.example.ui.theme.DayFlowOnSurfaceVariant
import com.example.ui.theme.DayFlowOutlineVariant
import com.example.ui.theme.DayFlowPrimary
import com.example.ui.theme.DayFlowSurfaceContainerLow
import com.example.ui.theme.DayFlowSurfaceContainerLowest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitProgressSheet(
  habit: HabitItem?,
  isOpen: Boolean,
  onDismiss: () -> Unit,
  onUpdateProgress: (habitId: String, newProgress: Int) -> Unit,
  onDeleteHabit: (habitId: String) -> Unit
) {
  if (!isOpen || habit == null) return

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var currentProgress by remember(habit.id, habit.currentProgress) {
    mutableIntStateOf(habit.currentProgress)
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = DayFlowSurfaceContainerLowest,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    dragHandle = null,
    modifier = Modifier.testTag("habit_progress_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 20.dp)
        .navigationBarsPadding()
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = habit.title,
            style = MaterialTheme.typography.headlineSmall.copy(
              fontWeight = FontWeight.Medium,
              fontSize = 22.sp
            ),
            color = DayFlowOnSurface
          )
          Text(
            text = "${habit.category.displayName} • ${habit.streakDays} day streak",
            style = MaterialTheme.typography.bodySmall,
            color = DayFlowOnSurfaceVariant
          )
        }

        IconButton(
          onClick = onDismiss,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = DayFlowOnSurfaceVariant,
            modifier = Modifier.size(22.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Stepper Section
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = DayFlowSurfaceContainerLow,
        border = BorderStroke(1.dp, DayFlowCardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Today's Progress",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 1.sp
            ),
            color = DayFlowOnSurfaceVariant
          )

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Surface(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .clickable {
                  if (currentProgress > 0) {
                    val step = if (habit.unit == "min") 5 else 1
                    currentProgress = (currentProgress - step).coerceAtLeast(0)
                  }
                },
              shape = CircleShape,
              color = DayFlowBackground,
              border = BorderStroke(1.dp, DayFlowOutlineVariant)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Remove,
                  contentDescription = "Decrease",
                  tint = DayFlowOnSurface,
                  modifier = Modifier.size(20.dp)
                )
              }
            }

            Spacer(modifier = Modifier.width(24.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "$currentProgress",
                style = MaterialTheme.typography.displaySmall.copy(
                  fontSize = 40.sp,
                  fontWeight = FontWeight.Normal
                ),
                color = DayFlowPrimary
              )
              Text(
                text = "of ${habit.dailyTarget} ${habit.unit}".trim(),
                style = MaterialTheme.typography.bodyMedium,
                color = DayFlowOnSurfaceVariant
              )
            }

            Spacer(modifier = Modifier.width(24.dp))

            Surface(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .clickable {
                  val step = if (habit.unit == "min") 5 else 1
                  currentProgress += step
                },
              shape = CircleShape,
              color = DayFlowBackground,
              border = BorderStroke(1.dp, DayFlowOutlineVariant)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = "Increase",
                  tint = DayFlowOnSurface,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Mark Complete (sets to target)
        OutlinedButton(
          onClick = {
            currentProgress = habit.dailyTarget
            onUpdateProgress(habit.id, habit.dailyTarget)
            onDismiss()
          },
          modifier = Modifier
            .weight(1f)
            .height(48.dp),
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, DayFlowOutlineVariant)
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = DayFlowPrimary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Target Done", color = DayFlowOnSurface, fontSize = 14.sp)
        }

        // Save Progress
        Button(
          onClick = {
            onUpdateProgress(habit.id, currentProgress)
            onDismiss()
          },
          modifier = Modifier
            .weight(1f)
            .height(48.dp),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = DayFlowPrimary,
            contentColor = DayFlowOnPrimary
          )
        ) {
          Text("Save Progress", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Delete habit option
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            onDeleteHabit(habit.id)
            onDismiss()
          }
          .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.DeleteOutline,
          contentDescription = "Delete Habit",
          tint = DayFlowOnSurfaceVariant.copy(alpha = 0.6f),
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Delete Habit",
          style = MaterialTheme.typography.bodySmall,
          color = DayFlowOnSurfaceVariant.copy(alpha = 0.6f)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}
