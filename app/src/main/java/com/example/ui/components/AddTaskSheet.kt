package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CustomCategory
import com.example.model.ItemCategory
import com.example.model.TaskItem
import com.example.model.TaskPriority
import com.example.model.TaskStatus
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOnPrimary
import com.example.ui.theme.DayFlowOnSurface
import com.example.ui.theme.DayFlowOnSurfaceVariant
import com.example.ui.theme.DayFlowOutlineVariant
import com.example.ui.theme.DayFlowSurfaceContainerHigh
import com.example.ui.theme.DayFlowSurfaceContainerLow
import com.example.ui.theme.DayFlowSurfaceContainerLowest
import com.example.util.CategoryIconHelper
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskSheet(
  isOpen: Boolean,
  taskToEdit: TaskItem? = null,
  customCategories: List<CustomCategory> = emptyList(),
  onDismiss: () -> Unit,
  onAddTask: (
    title: String,
    description: String,
    category: ItemCategory,
    priority: TaskPriority,
    time: String,
    durationMinutes: Int
  ) -> Unit,
  onUpdateTask: ((TaskItem) -> Unit)? = null,
  onDeleteTask: ((String) -> Unit)? = null,
  onCreateCustomCategory: ((CustomCategory) -> Unit)? = null
) {
  if (!isOpen) return

  val isEditing = taskToEdit != null
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var title by remember(taskToEdit) { mutableStateOf(taskToEdit?.title ?: "") }
  var description by remember(taskToEdit) { mutableStateOf(taskToEdit?.description ?: "") }
  var startTime by remember(taskToEdit) { mutableStateOf(taskToEdit?.time ?: "09:00 AM") }
  var estimatedMinutes by remember(taskToEdit) {
    mutableIntStateOf(taskToEdit?.estimatedMinutes ?: 30)
  }
  var endTime by remember(taskToEdit) {
    mutableStateOf(
      taskToEdit?.endTime ?: calculateDerivedEndTime(taskToEdit?.time ?: "09:00 AM", taskToEdit?.estimatedMinutes ?: 30)
    )
  }
  var selectedCategory by remember(taskToEdit) {
    mutableStateOf(taskToEdit?.category ?: ItemCategory.WORK)
  }
  var selectedStatus by remember(taskToEdit) {
    mutableStateOf(taskToEdit?.status ?: TaskStatus.PENDING)
  }
  var exceptionReason by remember(taskToEdit) {
    mutableStateOf(taskToEdit?.exceptionReason ?: "")
  }

  var showCreateCategoryDialog by remember { mutableStateOf(false) }
  var showDeleteConfirmDialog by remember { mutableStateOf(false) }

  val quickDurations = listOf(15, 30, 45, 60, 90, 120)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = DayFlowSurfaceContainerLowest,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    dragHandle = null,
    modifier = Modifier.testTag("add_task_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 20.dp)
        .navigationBarsPadding()
    ) {
      // Header: New Task / Edit Task + Close Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (isEditing) "Edit Task" else "New Task",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal
          ),
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.testTag("add_task_header")
        )

        IconButton(
          onClick = onDismiss,
          modifier = Modifier
            .size(36.dp)
            .testTag("close_add_task")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = DayFlowOnSurfaceVariant,
            modifier = Modifier.size(22.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Ghost Input: Task Title ("What needs to be done?")
      GhostInputField(
        value = title,
        onValueChange = { title = it },
        placeholder = "What needs to be done?",
        textStyle = MaterialTheme.typography.titleLarge.copy(
          fontSize = 18.sp,
          fontWeight = FontWeight.Normal,
          color = DayFlowOnSurface
        ),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("task_title_input")
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Ghost Input: Details ("Add some details...")
      GhostInputField(
        value = description,
        onValueChange = { description = it },
        placeholder = "Add some details...",
        textStyle = MaterialTheme.typography.bodyLarge.copy(
          fontSize = 15.sp,
          fontWeight = FontWeight.Normal,
          color = DayFlowOnSurface
        ),
        singleLine = false,
        minLines = 2,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("task_desc_input")
      )

      Spacer(modifier = Modifier.height(28.dp))

      // TIME & DURATION Section
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "SCHEDULE & TIME",
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp
          ),
          color = DayFlowOnSurfaceVariant
        )

        Text(
          text = "$estimatedMinutes min",
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          ),
          color = MaterialTheme.colorScheme.primary
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Start Time & End Time Inputs
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Start Time Input
        TimeSlotInput(
          label = "Start",
          value = startTime,
          placeholder = "09:00 AM",
          onValueChange = { newStart ->
            startTime = newStart
            endTime = calculateDerivedEndTime(newStart, estimatedMinutes)
          },
          modifier = Modifier
            .weight(1f)
            .testTag("start_time_input")
        )

        Text(
          text = "to",
          style = MaterialTheme.typography.bodyMedium,
          color = DayFlowOnSurfaceVariant.copy(alpha = 0.6f)
        )

        // End Time Input
        TimeSlotInput(
          label = "End",
          value = endTime,
          placeholder = "09:30 AM",
          onValueChange = { newEnd ->
            endTime = newEnd
            val diff = calculateTimeDifferenceMinutes(startTime, newEnd)
            if (diff != null && diff > 0) {
              estimatedMinutes = diff
            }
          },
          modifier = Modifier
            .weight(1f)
            .testTag("end_time_input")
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Quick Duration Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        quickDurations.forEach { mins ->
          val isSelected = estimatedMinutes == mins
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else DayFlowSurfaceContainerLow,
            border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else DayFlowOutlineVariant),
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .clickable {
                estimatedMinutes = mins
                endTime = calculateDerivedEndTime(startTime, mins)
              }
              .testTag("duration_chip_$mins")
          ) {
            Text(
              text = if (mins < 60) "${mins}m" else "${mins / 60}h ${if (mins % 60 > 0) "${mins % 60}m" else ""}".trim(),
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
              ),
              color = if (isSelected) MaterialTheme.colorScheme.primary else DayFlowOnSurfaceVariant,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      // CATEGORY Section
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "CATEGORY",
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp
          ),
          color = DayFlowOnSurfaceVariant
        )

        // Add Custom Category Button
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { showCreateCategoryDialog = true }
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .testTag("new_category_button"),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = "New Category",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.primary
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Category Chips
      val displayCategories = listOf(
        ItemCategory.WORK,
        ItemCategory.PERSONAL,
        ItemCategory.HEALTH,
        ItemCategory.LEARNING,
        ItemCategory.FITNESS,
        ItemCategory.MINDFULNESS,
        ItemCategory.FINANCE,
        ItemCategory.STUDY,
        ItemCategory.PROJECTS,
        ItemCategory.HOME
      )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        displayCategories.forEach { cat ->
          val isSelected = selectedCategory == cat
          CategorySelectionChip(
            label = cat.displayName,
            icon = CategoryIconHelper.getIconForCategory(cat),
            isSelected = isSelected,
            onClick = { selectedCategory = cat }
          )
        }

        // Custom Categories
        customCategories.forEach { customCat ->
          val mappedEnum = ItemCategory.fromName(customCat.name)
          val isSelected = selectedCategory == mappedEnum || selectedCategory.displayName.equals(customCat.name, ignoreCase = true)
          CategorySelectionChip(
            label = customCat.name,
            icon = CategoryIconHelper.getIconByName(customCat.iconName),
            isSelected = isSelected,
            onClick = { selectedCategory = mappedEnum }
          )
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      // STATUS Section
      Text(
        text = "STATUS",
        style = MaterialTheme.typography.labelSmall.copy(
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 1.2.sp
        ),
        color = DayFlowOnSurfaceVariant
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(
          TaskStatus.PENDING to "Pending",
          TaskStatus.COMPLETED to "Completed",
          TaskStatus.EXCEPTION to "Exception"
        ).forEach { (status, label) ->
          val isSelected = selectedStatus == status
          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .clickable { selectedStatus = status }
              .testTag("task_status_option_${label.lowercase()}"),
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else DayFlowSurfaceContainerLow,
            border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else DayFlowOutlineVariant)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontSize = 13.sp,
                  fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (isSelected) MaterialTheme.colorScheme.primary else DayFlowOnSurfaceVariant
              )
            }
          }
        }
      }

      if (selectedStatus == TaskStatus.EXCEPTION) {
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          color = DayFlowSurfaceContainerLow,
          border = BorderStroke(1.dp, DayFlowCardBorder)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "EXCEPTION REASON (OPTIONAL)",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp
              ),
              color = DayFlowOnSurfaceVariant.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(6.dp))
            BasicTextField(
              value = exceptionReason,
              onValueChange = { exceptionReason = it },
              textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = DayFlowOnSurface,
                fontSize = 14.sp
              ),
              cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("add_task_exception_reason_input"),
              decorationBox = { innerTextField ->
                if (exceptionReason.isEmpty()) {
                  Text(
                    text = "e.g., Postponed due to urgent client meeting",
                    style = MaterialTheme.typography.bodyMedium.copy(
                      color = DayFlowOnSurfaceVariant.copy(alpha = 0.5f),
                      fontSize = 14.sp
                    )
                  )
                }
                innerTextField()
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Bottom Action: Create / Save Task Button
      Button(
        onClick = {
          if (title.isNotBlank()) {
            val formattedStart = if (startTime.isNotBlank()) startTime else "09:00 AM"
            val formattedEnd = if (endTime.isNotBlank()) endTime else calculateDerivedEndTime(formattedStart, estimatedMinutes)

            if (isEditing && taskToEdit != null && onUpdateTask != null) {
              onUpdateTask(
                taskToEdit.copy(
                  title = title.trim(),
                  description = description.trim(),
                  category = selectedCategory,
                  status = selectedStatus,
                  exceptionReason = if (selectedStatus == TaskStatus.EXCEPTION) exceptionReason.trim().ifBlank { null } else null,
                  time = formattedStart,
                  endTime = formattedEnd,
                  estimatedMinutes = estimatedMinutes
                )
              )
            } else {
              onAddTask(
                title.trim(),
                description.trim(),
                selectedCategory,
                TaskPriority.MEDIUM,
                formattedStart,
                estimatedMinutes
              )
            }
            onDismiss()
          }
        },
        enabled = title.isNotBlank(),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("submit_task_button"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = DayFlowOnPrimary,
          disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
          disabledContentColor = DayFlowOnPrimary.copy(alpha = 0.7f)
        )
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isEditing) "Save Changes" else "Create Task",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Medium,
              fontSize = 16.sp
            )
          )
        }
      }

      if (isEditing && taskToEdit != null && onDeleteTask != null) {
        Spacer(modifier = Modifier.height(14.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              showDeleteConfirmDialog = true
            }
            .padding(vertical = 8.dp)
            .testTag("delete_task_button"),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Outlined.DeleteOutline,
            contentDescription = "Delete Task",
            tint = DayFlowOnSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Delete Task",
            style = MaterialTheme.typography.bodyMedium,
            color = DayFlowOnSurfaceVariant.copy(alpha = 0.7f)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }

  // Create Category Dialog
  CreateCategoryDialog(
    isOpen = showCreateCategoryDialog,
    onDismiss = { showCreateCategoryDialog = false },
    onCategoryCreated = { newCategory ->
      onCreateCustomCategory?.invoke(newCategory)
      selectedCategory = ItemCategory.fromName(newCategory.name)
    }
  )

  // Delete Confirmation Dialog
  DayFlowDeleteConfirmDialog(
    isOpen = showDeleteConfirmDialog,
    itemTitle = taskToEdit?.title ?: "",
    itemType = "Task",
    onConfirmDelete = {
      if (taskToEdit != null && onDeleteTask != null) {
        onDeleteTask(taskToEdit.id)
        onDismiss()
      }
    },
    onDismiss = { showDeleteConfirmDialog = false }
  )
}

@Composable
private fun GhostInputField(
  value: String,
  onValueChange: (String) -> Unit,
  placeholder: String,
  textStyle: TextStyle,
  singleLine: Boolean,
  minLines: Int = 1,
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier) {
    if (value.isEmpty()) {
      Text(
        text = placeholder,
        style = textStyle.copy(color = DayFlowOnSurfaceVariant.copy(alpha = 0.5f))
      )
    }
    BasicTextField(
      value = value,
      onValueChange = onValueChange,
      textStyle = textStyle,
      singleLine = singleLine,
      minLines = minLines,
      cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
      keyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.Sentences,
        imeAction = if (singleLine) ImeAction.Next else ImeAction.Default
      ),
      modifier = Modifier.fillMaxWidth()
    )
  }
}

@Composable
private fun TimeSlotInput(
  label: String,
  value: String,
  placeholder: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = DayFlowSurfaceContainerLow,
    border = BorderStroke(1.dp, DayFlowOutlineVariant),
    modifier = modifier
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = label.uppercase(Locale.US),
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
          ),
          color = DayFlowOnSurfaceVariant.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Box {
          if (value.isEmpty()) {
            Text(
              text = placeholder,
              style = MaterialTheme.typography.bodyMedium.copy(
                color = DayFlowOnSurfaceVariant.copy(alpha = 0.5f)
              )
            )
          }
          BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
              color = DayFlowOnSurface,
              fontWeight = FontWeight.Medium
            ),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
          )
        }
      }

      Icon(
        imageVector = Icons.Outlined.AccessTime,
        contentDescription = "Time",
        tint = DayFlowOnSurfaceVariant.copy(alpha = 0.7f),
        modifier = Modifier.size(16.dp)
      )
    }
  }
}

@Composable
private fun CategorySelectionChip(
  label: String,
  icon: ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    modifier = Modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .testTag("category_chip_${label.lowercase()}"),
    shape = RoundedCornerShape(16.dp),
    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else DayFlowSurfaceContainerLow,
    border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else DayFlowOutlineVariant)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) MaterialTheme.colorScheme.primary else DayFlowOnSurfaceVariant,
        modifier = Modifier.size(15.dp)
      )
      Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontSize = 13.sp,
          fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        ),
        color = if (isSelected) MaterialTheme.colorScheme.primary else DayFlowOnSurfaceVariant
      )
    }
  }
}

private fun parseTimeMinutes(timeStr: String): Int? {
  return try {
    val clean = timeStr.trim().uppercase(Locale.US)
    val parts = clean.split(" ")
    val timeParts = parts[0].split(":")
    var hour = timeParts[0].toInt()
    val min = if (timeParts.size > 1) timeParts[1].toInt() else 0
    val isPm = parts.size > 1 && parts[1].contains("PM")
    val isAm = parts.size > 1 && parts[1].contains("AM")

    if (isPm && hour < 12) hour += 12
    if (isAm && hour == 12) hour = 0

    hour * 60 + min
  } catch (_: Exception) {
    null
  }
}

private fun calculateDerivedEndTime(startTime: String, durationMinutes: Int): String {
  val startMin = parseTimeMinutes(startTime) ?: (9 * 60)
  val endMin = (startMin + durationMinutes) % 1440
  val hour24 = endMin / 60
  val minute = endMin % 60
  val amPm = if (hour24 >= 12) "PM" else "AM"
  val hour12 = when {
    hour24 == 0 -> 12
    hour24 > 12 -> hour24 - 12
    else -> hour24
  }
  return String.format(Locale.US, "%02d:%02d %s", hour12, minute, amPm)
}

private fun calculateTimeDifferenceMinutes(startTime: String, endTime: String): Int? {
  val start = parseTimeMinutes(startTime) ?: return null
  val end = parseTimeMinutes(endTime) ?: return null
  val diff = end - start
  return if (diff <= 0) diff + 1440 else diff
}
