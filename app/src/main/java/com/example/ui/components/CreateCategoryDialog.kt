package com.example.ui.components

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CustomCategory
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOnPrimary
import com.example.ui.theme.DayFlowOnSurface
import com.example.ui.theme.DayFlowOnSurfaceVariant
import com.example.ui.theme.DayFlowOutlineVariant
import com.example.ui.theme.DayFlowSurfaceContainerLow
import com.example.ui.theme.DayFlowSurfaceContainerLowest
import com.example.ui.theme.DayFlowSurfaceVariant
import com.example.util.CategoryIconHelper
import java.util.UUID

@Composable
fun CreateCategoryDialog(
  isOpen: Boolean,
  onDismiss: () -> Unit,
  onCategoryCreated: (CustomCategory) -> Unit
) {
  if (!isOpen) return

  var categoryName by remember { mutableStateOf("") }
  var isAutoIcon by remember { mutableStateOf(true) }
  var selectedIconId by remember { mutableStateOf("work") }

  // Derive active icon
  val currentEffectiveIconId = if (isAutoIcon) {
    if (categoryName.isNotBlank()) CategoryIconHelper.inferIconForName(categoryName) else "category"
  } else {
    selectedIconId
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      dismissOnBackPress = true,
      dismissOnClickOutside = true,
      usePlatformDefaultWidth = false
    )
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = DayFlowSurfaceContainerLowest,
      border = BorderStroke(1.dp, DayFlowCardBorder),
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(vertical = 16.dp)
        .testTag("create_category_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.primaryContainer,
              modifier = Modifier.size(38.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = CategoryIconHelper.getIconByName(currentEffectiveIconId),
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(20.dp)
                )
              }
            }

            Column {
              Text(
                text = "New Category",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontSize = 18.sp,
                  fontWeight = FontWeight.SemiBold
                ),
                color = DayFlowOnSurface
              )
              Text(
                text = "Add a custom task classification",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = DayFlowOnSurfaceVariant
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Outlined.Close,
              contentDescription = "Close",
              tint = DayFlowOnSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Input Field
        Text(
          text = "CATEGORY NAME",
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
          ),
          color = DayFlowOnSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = categoryName,
          onValueChange = { categoryName = it },
          placeholder = { Text("e.g. Finance, Piano, Gardening...", fontSize = 14.sp) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = DayFlowOutlineVariant,
            focusedContainerColor = DayFlowSurfaceContainerLow,
            unfocusedContainerColor = DayFlowSurfaceContainerLow,
            focusedTextColor = DayFlowOnSurface,
            unfocusedTextColor = DayFlowOnSurface
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("category_name_input")
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Icon Selection Section
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "SELECT ICON",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 1.sp
            ),
            color = DayFlowOnSurfaceVariant
          )

          // Auto Icon Toggle Pill
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isAutoIcon) MaterialTheme.colorScheme.primaryContainer else DayFlowSurfaceContainerLow,
            border = BorderStroke(1.dp, if (isAutoIcon) MaterialTheme.colorScheme.primary else DayFlowOutlineVariant),
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .clickable { isAutoIcon = !isAutoIcon }
              .testTag("auto_icon_toggle")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = if (isAutoIcon) MaterialTheme.colorScheme.primary else DayFlowOnSurfaceVariant,
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = if (isAutoIcon) "Auto Match (On)" else "Auto Match",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                ),
                color = if (isAutoIcon) MaterialTheme.colorScheme.primary else DayFlowOnSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Icon Grid (Monochrome outline icons)
        LazyVerticalGrid(
          columns = GridCells.Fixed(5),
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 180.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(CategoryIconHelper.availableIcons, key = { it.id }) { iconOpt ->
            val isSelected = (!isAutoIcon && selectedIconId == iconOpt.id) ||
              (isAutoIcon && currentEffectiveIconId == iconOpt.id)

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else DayFlowSurfaceContainerLow,
              border = BorderStroke(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else DayFlowSurfaceVariant
              ),
              modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                  isAutoIcon = false
                  selectedIconId = iconOpt.id
                }
                .testTag("icon_option_${iconOpt.id}")
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = iconOpt.icon,
                  contentDescription = iconOpt.label,
                  tint = if (isSelected) MaterialTheme.colorScheme.primary else DayFlowOnSurfaceVariant,
                  modifier = Modifier.size(22.dp)
                )
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
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("cancel_create_category"),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, DayFlowOutlineVariant)
          ) {
            Text(
              text = "Cancel",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
              color = DayFlowOnSurface
            )
          }

          Button(
            onClick = {
              if (categoryName.isNotBlank()) {
                val newCat = CustomCategory(
                  id = UUID.randomUUID().toString(),
                  name = categoryName.trim(),
                  iconName = currentEffectiveIconId,
                  colorHex = 0xFF3B82F6
                )
                onCategoryCreated(newCat)
                onDismiss()
              }
            },
            enabled = categoryName.isNotBlank(),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("save_create_category"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = DayFlowOnPrimary,
              disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
              disabledContentColor = DayFlowOnPrimary.copy(alpha = 0.7f)
            )
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Create",
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.SemiBold
                )
              )
            }
          }
        }
      }
    }
  }
}
