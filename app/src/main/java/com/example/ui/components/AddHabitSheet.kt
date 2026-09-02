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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ItemCategory
import com.example.ui.theme.DayFlowBackground
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOnPrimary
import com.example.ui.theme.DayFlowOnSurface
import com.example.ui.theme.DayFlowOnSurfaceVariant
import com.example.ui.theme.DayFlowOutlineVariant
import com.example.ui.theme.DayFlowSurfaceContainerLow
import com.example.ui.theme.DayFlowSurfaceContainerLowest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitSheet(
  isOpen: Boolean,
  onDismiss: () -> Unit,
  onAddHabit: (
    title: String,
    category: ItemCategory,
    dailyTarget: Int,
    unit: String,
    reminderTime: String
  ) -> Unit
) {
  if (!isOpen) return

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var title by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf(ItemCategory.HEALTH) }
  var selectedUnitType by remember { mutableStateOf("Count") }
  var dailyTarget by remember { mutableIntStateOf(1) }

  val unitPresets = listOf("Count", "L", "min", "pages", "times")

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = DayFlowSurfaceContainerLowest,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    dragHandle = null,
    modifier = Modifier.testTag("add_habit_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 20.dp)
        .navigationBarsPadding()
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "New Habit",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal
          ),
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.testTag("add_habit_header")
        )

        IconButton(
          onClick = onDismiss,
          modifier = Modifier
            .size(36.dp)
            .testTag("close_add_habit")
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

      // Habit Name Input
      Box(modifier = Modifier.fillMaxWidth()) {
        if (title.isEmpty()) {
          Text(
            text = "Habit name (e.g., Hydration, Reading)",
            style = MaterialTheme.typography.titleLarge.copy(
              fontSize = 18.sp,
              fontWeight = FontWeight.Normal,
              color = DayFlowOnSurfaceVariant.copy(alpha = 0.5f)
            )
          )
        }
        BasicTextField(
          value = title,
          onValueChange = { title = it },
          textStyle = MaterialTheme.typography.titleLarge.copy(
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            color = DayFlowOnSurface
          ),
          singleLine = true,
          cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
          keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("habit_name_input")
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Category Selection
      Text(
        text = "CATEGORY",
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
        listOf(ItemCategory.HEALTH, ItemCategory.LEARNING, ItemCategory.MINDFULNESS).forEach { cat ->
          val isSelected = selectedCategory == cat
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .clickable { selectedCategory = cat }
              .testTag("habit_cat_${cat.name.lowercase()}"),
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
            border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else DayFlowOutlineVariant)
          ) {
            Text(
              text = cat.displayName,
              style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
              color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else DayFlowOnSurfaceVariant,
              fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(ItemCategory.FITNESS, ItemCategory.WORK, ItemCategory.PERSONAL).forEach { cat ->
          val isSelected = selectedCategory == cat
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .clickable { selectedCategory = cat }
              .testTag("habit_cat_${cat.name.lowercase()}"),
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
            border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else DayFlowOutlineVariant)
          ) {
            Text(
              text = cat.displayName,
              style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
              color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else DayFlowOnSurfaceVariant,
              fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Unit Type & Daily Target
      Text(
        text = "DAILY TARGET & UNIT",
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
        unitPresets.forEach { unit ->
          val isSelected = selectedUnitType == unit
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .clickable {
                selectedUnitType = unit
                if (unit == "L" && dailyTarget == 1) dailyTarget = 3
                if (unit == "min" && dailyTarget == 1) dailyTarget = 30
                if (unit == "pages" && dailyTarget == 1) dailyTarget = 20
              }
              .testTag("unit_chip_$unit"),
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
            border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else DayFlowOutlineVariant)
          ) {
            Text(
              text = unit,
              style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
              color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else DayFlowOnSurfaceVariant,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Stepper for Daily Target
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = DayFlowSurfaceContainerLow,
        border = BorderStroke(1.dp, DayFlowCardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Target per day",
            style = MaterialTheme.typography.bodyMedium,
            color = DayFlowOnSurface
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Surface(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable { if (dailyTarget > 1) dailyTarget -= 1 },
              shape = CircleShape,
              color = DayFlowBackground,
              border = BorderStroke(1.dp, DayFlowOutlineVariant)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Remove,
                  contentDescription = "Decrease",
                  modifier = Modifier.size(16.dp),
                  tint = DayFlowOnSurface
                )
              }
            }

            Text(
              text = "$dailyTarget ${if (selectedUnitType != "Count") selectedUnitType else ""}".trim(),
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.testTag("target_value_display")
            )

            Surface(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable { dailyTarget += 1 },
              shape = CircleShape,
              color = DayFlowBackground,
              border = BorderStroke(1.dp, DayFlowOutlineVariant)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = "Increase",
                  modifier = Modifier.size(16.dp),
                  tint = DayFlowOnSurface
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Create Habit Button
      Button(
        onClick = {
          if (title.isNotBlank()) {
            val unitStr = if (selectedUnitType == "Count") "" else selectedUnitType
            onAddHabit(
              title.trim(),
              selectedCategory,
              dailyTarget,
              unitStr,
              "08:00 AM"
            )
            onDismiss()
          }
        },
        enabled = title.isNotBlank(),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("submit_habit_button"),
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
            text = "Create Habit",
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
