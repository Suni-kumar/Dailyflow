package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.DayFlowBackground

@Composable
fun DayFlowTopBar(
  title: String = "DayFlow",
  subtitle: String? = null,
  streakCount: Int = 0,
  onProfileClick: () -> Unit = {},
  onSettingsClick: () -> Unit = {}
) {
  Surface(
    color = Color.Transparent,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .height(56.dp)
        .padding(horizontal = 20.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left: App title
      Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall.copy(
          fontSize = 24.sp,
          fontWeight = FontWeight.SemiBold,
          letterSpacing = (-0.5).sp
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.testTag("top_bar_title")
      )

      // Right: Settings Icon
      IconButton(
        onClick = onSettingsClick,
        modifier = Modifier
          .size(40.dp)
          .testTag("top_bar_settings_button")
      ) {
        Icon(
          imageVector = Icons.Outlined.Settings,
          contentDescription = "Settings",
          tint = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.size(24.dp)
        )
      }
    }
  }
}

