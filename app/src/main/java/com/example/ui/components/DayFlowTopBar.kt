package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DayFlowBackground
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOutlineVariant
import com.example.ui.theme.DayFlowPrimary
import com.example.ui.theme.DayFlowPrimaryFixed

@Composable
fun DayFlowTopBar(
  title: String = "DayFlow",
  subtitle: String? = null,
  streakCount: Int = 14,
  onProfileClick: () -> Unit = {},
  onSettingsClick: () -> Unit = {}
) {
  Surface(
    color = MaterialTheme.colorScheme.background,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .height(60.dp)
        .padding(horizontal = 20.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left: Profile Avatar
      IconButton(
        onClick = onProfileClick,
        modifier = Modifier
          .size(36.dp)
          .testTag("top_bar_profile_button")
      ) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(DayFlowPrimaryFixed)
            .border(1.dp, DayFlowOutlineVariant, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = "Profile",
            tint = DayFlowPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      // Center: Title "DayFlow"
      Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium.copy(
          fontSize = 24.sp,
          fontWeight = FontWeight.Normal,
          letterSpacing = (-0.3).sp
        ),
        color = DayFlowPrimary,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .weight(1f)
          .testTag("top_bar_title")
      )

      // Right: Settings Icon
      IconButton(
        onClick = onSettingsClick,
        modifier = Modifier
          .size(36.dp)
          .testTag("top_bar_settings_button")
      ) {
        Icon(
          imageVector = Icons.Outlined.Settings,
          contentDescription = "Settings",
          tint = DayFlowPrimary,
          modifier = Modifier.size(24.dp)
        )
      }
    }
  }
}
