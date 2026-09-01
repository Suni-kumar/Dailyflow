package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WorkOutline
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
import com.example.model.ItemCategory
import com.example.model.TaskPriority
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOnPrimary
import com.example.ui.theme.DayFlowOnPrimaryContainer
import com.example.ui.theme.DayFlowOnSurface
import com.example.ui.theme.DayFlowOnSurfaceVariant
import com.example.ui.theme.DayFlowOutlineVariant
import com.example.ui.theme.DayFlowPrimary
import com.example.ui.theme.DayFlowPrimaryContainer
import com.example.ui.theme.DayFlowSurfaceContainerLowest

enum class StitchCategory(
  val label: String,
  val icon: ImageVector,
  val mappedCategory: ItemCategory
) {
  WORK("Work", Icons.Outlined.WorkOutline, ItemCategory.WORK),
  PERSONAL("Personal", Icons.Outlined.Person, ItemCategory.PERSONAL),
  HEALTH("Health", Icons.Outlined.FavoriteBorder, ItemCategory.HEALTH),
  NEW("New", Icons.Default.Add, ItemCategory.LEARNING)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskSheet(
  isOpen: Boolean,
  onDismiss: () -> Unit,
  onAddTask: (
    title: String,
    description: String,
    category: ItemCategory,
    priority: TaskPriority,
    time: String,
    durationMinutes: Int
  ) -> Unit
) {
  if (!isOpen) return

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf(StitchCategory.WORK) }
  var startTime by remember { mutableStateOf("") }
  var endTime by remember { mutableStateOf("") }

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
      // Header: New Task + Close Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "New Task",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal
          ),
          color = DayFlowPrimary,
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

      Spacer(modifier = Modifier.height(28.dp))

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

      Spacer(modifier = Modifier.height(24.dp))

      // Ghost Input: Details ("Add some details...")
      GhostInputField(
        value = description,
        onValueChange = { description = it },
        placeholder = "Add some details...",
        textStyle = MaterialTheme.typography.bodyLarge.copy(
          fontSize = 16.sp,
          fontWeight = FontWeight.Normal,
          color = DayFlowOnSurface
        ),
        singleLine = false,
        minLines = 3,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("task_desc_input")
      )

      Spacer(modifier = Modifier.height(32.dp))

      // TIME Section
      Text(
        text = "TIME",
        style = MaterialTheme.typography.labelSmall.copy(
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 1.2.sp
        ),
        color = DayFlowOnSurfaceVariant
      )

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Start Time Input
        TimeSlotInput(
          value = startTime,
          placeholder = "--:-- --",
          onValueChange = { startTime = it },
          modifier = Modifier
            .weight(1f)
            .testTag("start_time_input")
        )

        Text(
          text = "to",
          style = MaterialTheme.typography.bodyLarge,
          color = DayFlowOnSurfaceVariant
        )

        // End Time Input
        TimeSlotInput(
          value = endTime,
          placeholder = "--:-- --",
          onValueChange = { endTime = it },
          modifier = Modifier
            .weight(1f)
            .testTag("end_time_input")
        )
      }

      Spacer(modifier = Modifier.height(32.dp))

      // CATEGORY Section
      Text(
        text = "CATEGORY",
        style = MaterialTheme.typography.labelSmall.copy(
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 1.2.sp
        ),
        color = DayFlowOnSurfaceVariant
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Category Chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StitchCategory.values().take(2).forEach { item ->
          StitchCategoryChip(
            category = item,
            isSelected = selectedCategory == item,
            onClick = { selectedCategory = item }
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StitchCategory.values().drop(2).forEach { item ->
          StitchCategoryChip(
            category = item,
            isSelected = selectedCategory == item,
            onClick = { selectedCategory = item }
          )
        }
      }

      Spacer(modifier = Modifier.height(36.dp))

      // Bottom Action: Create Task Button
      Button(
        onClick = {
          if (title.isNotBlank()) {
            val formattedTime = if (startTime.isNotBlank()) startTime else "9:00 AM"
            onAddTask(
              title.trim(),
              description.trim(),
              selectedCategory.mappedCategory,
              TaskPriority.MEDIUM,
              formattedTime,
              30
            )
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
          containerColor = DayFlowPrimary,
          contentColor = DayFlowOnPrimary,
          disabledContainerColor = DayFlowPrimary.copy(alpha = 0.5f),
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
            text = "Create Task",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Medium,
              fontSize = 16.sp
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
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
      cursorBrush = SolidColor(DayFlowPrimary),
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
  value: String,
  placeholder: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .padding(bottom = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Box(modifier = Modifier.weight(1f)) {
      if (value.isEmpty()) {
        Text(
          text = placeholder,
          style = MaterialTheme.typography.bodyLarge.copy(
            color = DayFlowOnSurfaceVariant.copy(alpha = 0.6f),
            letterSpacing = 1.sp
          )
        )
      }
      BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = DayFlowOnSurface),
        singleLine = true,
        cursorBrush = SolidColor(DayFlowPrimary),
        modifier = Modifier.fillMaxWidth()
      )
    }

    Icon(
      imageVector = Icons.Outlined.AccessTime,
      contentDescription = "Time",
      tint = DayFlowOnSurfaceVariant,
      modifier = Modifier.size(18.dp)
    )
  }
}

@Composable
private fun StitchCategoryChip(
  category: StitchCategory,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    modifier = Modifier
      .clip(RoundedCornerShape(20.dp))
      .clickable { onClick() }
      .testTag("category_chip_${category.label.lowercase()}"),
    shape = RoundedCornerShape(20.dp),
    color = if (isSelected) DayFlowPrimaryContainer else Color.Transparent,
    border = BorderStroke(1.dp, if (isSelected) DayFlowPrimary else DayFlowOutlineVariant)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Icon(
        imageVector = category.icon,
        contentDescription = null,
        tint = if (isSelected) DayFlowOnPrimaryContainer else DayFlowOnSurfaceVariant,
        modifier = Modifier.size(16.dp)
      )
      Text(
        text = category.label,
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
        color = if (isSelected) DayFlowOnPrimaryContainer else DayFlowOnSurfaceVariant,
        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
      )
    }
  }
}
