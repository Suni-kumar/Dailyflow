package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TaskItem
import com.example.model.TaskStatus
import com.example.ui.theme.DayFlowBackground
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOnPrimary
import com.example.ui.theme.DayFlowOnSurface
import com.example.ui.theme.DayFlowOnSurfaceSubtle
import com.example.ui.theme.DayFlowOnSurfaceVariant
import com.example.ui.theme.DayFlowOutlineVariant
import com.example.ui.theme.DayFlowSurface
import com.example.ui.theme.DayFlowSurfaceContainerHigh
import com.example.ui.theme.DayFlowSurfaceContainerLow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskStatusSelectorSheet(
  task: TaskItem,
  onSelectStatus: (TaskStatus, String?) -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var selectedStatus by remember { mutableStateOf(task.status) }
  var exceptionReason by remember { mutableStateOf(task.exceptionReason ?: "") }

  val quickReasons = listOf(
    "Postponed to tomorrow",
    "External blocker / waiting",
    "No longer needed today",
    "Schedule conflict",
    "Low energy / rest priority"
  )

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = DayFlowSurface,
    scrimColor = Color.Black.copy(alpha = 0.5f),
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(top = 12.dp, bottom = 8.dp)
          .size(width = 36.dp, height = 4.dp)
          .clip(CircleShape)
          .background(DayFlowOnSurfaceSubtle.copy(alpha = 0.35f))
      )
    },
    modifier = Modifier.testTag("task_status_selector_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .navigationBarsPadding()
        .padding(bottom = 28.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Update Task Status",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.SemiBold,
              fontSize = 18.sp
            ),
            color = DayFlowOnSurface
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = task.title,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
            color = DayFlowOnSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("close_status_sheet_button")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = DayFlowOnSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Status Options List
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        StatusOptionCard(
          status = TaskStatus.PENDING,
          title = "Pending",
          subtitle = "Active & scheduled for today",
          icon = Icons.Default.RadioButtonUnchecked,
          iconTint = DayFlowOnSurfaceVariant,
          isSelected = selectedStatus == TaskStatus.PENDING,
          onClick = { selectedStatus = TaskStatus.PENDING },
          testTag = "status_option_pending"
        )

        StatusOptionCard(
          status = TaskStatus.COMPLETED,
          title = "Completed",
          subtitle = "Finished successfully",
          icon = Icons.Default.CheckCircle,
          iconTint = MaterialTheme.colorScheme.primary,
          isSelected = selectedStatus == TaskStatus.COMPLETED,
          onClick = { selectedStatus = TaskStatus.COMPLETED },
          testTag = "status_option_completed"
        )

        StatusOptionCard(
          status = TaskStatus.EXCEPTION,
          title = "Exception",
          subtitle = "Intentionally deferred or skipped",
          icon = Icons.Default.RemoveCircleOutline,
          iconTint = MaterialTheme.colorScheme.tertiary,
          isSelected = selectedStatus == TaskStatus.EXCEPTION,
          onClick = { selectedStatus = TaskStatus.EXCEPTION },
          testTag = "status_option_exception"
        )
      }

      // Exception Reason Input & Chips
      AnimatedVisibility(visible = selectedStatus == TaskStatus.EXCEPTION) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
        ) {
          Text(
            text = "REASON (OPTIONAL)",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 1.sp
            ),
            color = DayFlowOnSurfaceVariant
          )

          Spacer(modifier = Modifier.height(8.dp))

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(quickReasons) { reason ->
              val isChosen = exceptionReason == reason
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isChosen) MaterialTheme.colorScheme.primaryContainer else DayFlowSurfaceContainerLow,
                border = BorderStroke(1.dp, if (isChosen) MaterialTheme.colorScheme.primary else DayFlowOutlineVariant),
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .clickable { exceptionReason = if (isChosen) "" else reason }
                  .testTag("reason_chip_${reason.take(8)}")
              ) {
                Text(
                  text = reason,
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                  color = if (isChosen) MaterialTheme.colorScheme.onPrimaryContainer else DayFlowOnSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = exceptionReason,
            onValueChange = { exceptionReason = it },
            placeholder = { Text("Note reason for skipping or postponing...", color = DayFlowOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 13.sp) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("exception_reason_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = DayFlowOutlineVariant,
              focusedContainerColor = DayFlowBackground,
              unfocusedContainerColor = DayFlowBackground,
              focusedTextColor = DayFlowOnSurface,
              unfocusedTextColor = DayFlowOnSurface
            ),
            shape = RoundedCornerShape(12.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        TextButton(
          onClick = onDismiss,
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("cancel_status_button")
        ) {
          Text(
            text = "Cancel",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
            color = DayFlowOnSurfaceVariant
          )
        }

        Button(
          onClick = {
            onSelectStatus(
              selectedStatus,
              if (selectedStatus == TaskStatus.EXCEPTION) exceptionReason.trim().ifEmpty { null } else null
            )
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = DayFlowOnPrimary
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .weight(1.5f)
            .height(48.dp)
            .testTag("confirm_status_button")
        ) {
          Text(
            text = "Apply Status",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
          )
        }
      }
    }
  }
}

@Composable
private fun StatusOptionCard(
  status: TaskStatus,
  title: String,
  subtitle: String,
  icon: ImageVector,
  iconTint: Color,
  isSelected: Boolean,
  onClick: () -> Unit,
  testTag: String
) {
  val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else DayFlowSurfaceContainerLow
  val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else DayFlowCardBorder

  Surface(
    shape = RoundedCornerShape(16.dp),
    color = backgroundColor,
    border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .testTag(testTag)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else DayFlowSurfaceContainerHigh),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = if (isSelected) MaterialTheme.colorScheme.primary else iconTint,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            fontSize = 15.sp
          ),
          color = DayFlowOnSurface
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
          color = DayFlowOnSurfaceVariant
        )
      }

      if (isSelected) {
        Box(
          modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Selected",
            tint = DayFlowOnPrimary,
            modifier = Modifier.size(14.dp)
          )
        }
      }
    }
  }
}
