package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CustomCategory
import com.example.model.HabitItem
import com.example.model.ItemCategory
import com.example.ui.theme.DayFlowBackground
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOnPrimary
import com.example.ui.theme.DayFlowOnSurface
import com.example.ui.theme.DayFlowOnSurfaceVariant
import com.example.ui.theme.DayFlowOutlineVariant
import com.example.ui.theme.DayFlowSurfaceContainerLow
import com.example.ui.theme.DayFlowSurfaceContainerLowest
import com.example.util.CategoryIconHelper

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
  ) -> Unit,
  habitToEdit: HabitItem? = null,
  onUpdateHabit: ((HabitItem) -> Unit)? = null,
  onDeleteHabit: ((String) -> Unit)? = null,
  customCategories: List<CustomCategory> = emptyList(),
  onCreateCustomCategory: ((CustomCategory) -> Unit)? = null
) {
  if (!isOpen) return

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val isEditing = habitToEdit != null

  var title by remember(habitToEdit?.id) { mutableStateOf(habitToEdit?.title ?: "") }
  var selectedCategory by remember(habitToEdit?.id) { mutableStateOf(habitToEdit?.category ?: ItemCategory.HEALTH) }
  var selectedUnitType by remember(habitToEdit?.id) {
    mutableStateOf(
      when (val u = habitToEdit?.unit) {
        null, "" -> "Count"
        else -> u
      }
    )
  }
  var dailyTarget by remember(habitToEdit?.id) { mutableIntStateOf(habitToEdit?.dailyTarget ?: 1) }
  var showCreateCategoryDialog by remember { mutableStateOf(false) }
  var showDeleteConfirmDialog by remember { mutableStateOf(false) }

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
          text = if (isEditing) "Edit Habit" else "New Habit",
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

      // Category Selection Section
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
        ItemCategory.HEALTH,
        ItemCategory.LEARNING,
        ItemCategory.MINDFULNESS,
        ItemCategory.FITNESS,
        ItemCategory.WORK,
        ItemCategory.PERSONAL,
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
            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else DayFlowSurfaceContainerLow,
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

      // Submit Button (Create / Save)
      Button(
        onClick = {
          if (title.isNotBlank()) {
            val unitStr = if (selectedUnitType == "Count") "" else selectedUnitType
            if (isEditing && habitToEdit != null && onUpdateHabit != null) {
              onUpdateHabit(
                habitToEdit.copy(
                  title = title.trim(),
                  category = selectedCategory,
                  dailyTarget = dailyTarget,
                  unit = unitStr
                )
              )
            } else {
              onAddHabit(
                title.trim(),
                selectedCategory,
                dailyTarget,
                unitStr,
                "08:00 AM"
              )
            }
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
            text = if (isEditing) "Save Changes" else "Create Habit",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Medium,
              fontSize = 16.sp
            )
          )
        }
      }

      // Delete habit option when editing
      if (isEditing && habitToEdit != null && onDeleteHabit != null) {
        Spacer(modifier = Modifier.height(14.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { showDeleteConfirmDialog = true }
            .padding(vertical = 8.dp)
            .testTag("delete_habit_button"),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = "Delete Habit",
            tint = DayFlowOnSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Delete Habit",
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
    itemTitle = habitToEdit?.title ?: "",
    itemType = "Habit",
    onConfirmDelete = {
      if (habitToEdit != null && onDeleteHabit != null) {
        onDeleteHabit(habitToEdit.id)
        onDismiss()
      }
    },
    onDismiss = { showDeleteConfirmDialog = false }
  )
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
