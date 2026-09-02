package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOnSurface
import com.example.ui.theme.DayFlowOnSurfaceVariant
import com.example.ui.theme.DayFlowOutlineVariant
import com.example.ui.theme.DayFlowSurfaceContainerLowest

@Composable
fun DayFlowDeleteConfirmDialog(
  isOpen: Boolean,
  itemTitle: String,
  itemType: String = "Item",
  onConfirmDelete: () -> Unit,
  onDismiss: () -> Unit
) {
  if (!isOpen) return

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      dismissOnBackPress = true,
      dismissOnClickOutside = true
    )
  ) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = DayFlowSurfaceContainerLowest,
      border = BorderStroke(1.dp, DayFlowCardBorder),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
        .testTag("delete_confirm_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
            modifier = Modifier.size(36.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Outlined.DeleteOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          Text(
            text = if (itemTitle.isNotBlank()) "Delete $itemTitle?" else "Delete $itemType?",
            style = MaterialTheme.typography.titleMedium.copy(
              fontSize = 18.sp,
              fontWeight = FontWeight.SemiBold
            ),
            color = DayFlowOnSurface,
            maxLines = 2
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "This action cannot be undone.",
          style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 14.sp
          ),
          color = DayFlowOnSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier
              .weight(1f)
              .height(46.dp)
              .testTag("delete_dialog_cancel"),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, DayFlowOutlineVariant)
          ) {
            Text(
              text = "Cancel",
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
              ),
              color = DayFlowOnSurface
            )
          }

          Button(
            onClick = {
              onConfirmDelete()
              onDismiss()
            },
            modifier = Modifier
              .weight(1f)
              .height(46.dp)
              .testTag("delete_dialog_confirm"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.error,
              contentColor = Color.White
            )
          ) {
            Text(
              text = "Delete",
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
              ),
              color = Color.White
            )
          }
        }
      }
    }
  }
}

@Composable
private fun Box(
  contentAlignment: Alignment,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  androidx.compose.foundation.layout.Box(
    modifier = modifier,
    contentAlignment = contentAlignment
  ) {
    content()
  }
}
