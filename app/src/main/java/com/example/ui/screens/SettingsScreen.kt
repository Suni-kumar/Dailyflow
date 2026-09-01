package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.WorkspacePremium
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DayFlowBackground
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOnPrimary
import com.example.ui.theme.DayFlowOnPrimaryContainer
import com.example.ui.theme.DayFlowOnSurface
import com.example.ui.theme.DayFlowOnSurfaceVariant
import com.example.ui.theme.DayFlowOutline
import com.example.ui.theme.DayFlowOutlineVariant
import com.example.ui.theme.DayFlowPrimary
import com.example.ui.theme.DayFlowPrimaryContainer
import com.example.ui.theme.DayFlowPrimaryFixed
import com.example.ui.theme.DayFlowSecondary
import com.example.ui.theme.DayFlowSurface
import com.example.ui.theme.DayFlowSurfaceContainerLow
import com.example.ui.theme.DayFlowSurfaceContainerLowest
import com.example.ui.theme.DayFlowSurfaceVariant
import com.example.ui.theme.DayFlowTertiary

private val DayFlowErrorRed = Color(0xFFBA1A1A)

@Composable
fun SettingsScreen(
  onNavigateBack: () -> Unit = {}
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(DayFlowBackground)
      .testTag("settings_screen")
  ) {
    // 1. Top Bar with back button and centered Title
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .height(60.dp)
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onNavigateBack,
        modifier = Modifier
          .size(40.dp)
          .testTag("settings_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = DayFlowOnSurfaceVariant
        )
      }

      Text(
        text = "Settings",
        style = MaterialTheme.typography.titleLarge.copy(
          fontSize = 20.sp,
          fontWeight = FontWeight.Normal
        ),
        color = DayFlowOnSurface
      )

      // Spacer for symmetry
      Box(modifier = Modifier.size(40.dp))
    }

    // 2. Settings Content
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
      // Premium Card
      item {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("settings_premium_card"),
          shape = RoundedCornerShape(16.dp),
          color = DayFlowSurfaceContainerLow,
          border = BorderStroke(1.dp, DayFlowOutlineVariant.copy(alpha = 0.5f))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Icon(
                imageVector = Icons.Filled.WorkspacePremium,
                contentDescription = null,
                tint = DayFlowPrimary,
                modifier = Modifier.size(24.dp)
              )
              Text(
                text = "DayFlow Premium",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Medium
                ),
                color = DayFlowOnSurface
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "Unlock serene data visualization and advanced mindful tracking tools.",
              style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 14.sp,
                lineHeight = 20.sp
              ),
              color = DayFlowOnSurfaceVariant
            )
          }
        }
      }

      // Group 1: ACCOUNT
      item {
        SettingsGroupSection(title = "ACCOUNT") {
          SettingsItemRow(
            icon = Icons.Outlined.Person,
            title = "Profile",
            value = null,
            onClick = {},
            testTag = "settings_item_profile"
          )

          SettingsRowDivider()

          SettingsItemRow(
            icon = Icons.Outlined.CloudSync,
            title = "Data Sync",
            value = "Just now",
            onClick = {},
            testTag = "settings_item_data_sync"
          )
        }
      }

      // Group 2: PREFERENCES
      item {
        SettingsGroupSection(title = "PREFERENCES") {
          SettingsItemRow(
            icon = Icons.Outlined.Notifications,
            title = "Notifications",
            value = null,
            onClick = {},
            testTag = "settings_item_notifications"
          )

          SettingsRowDivider()

          SettingsItemRow(
            icon = Icons.Outlined.Palette,
            title = "Theme",
            value = "System default",
            onClick = {},
            testTag = "settings_item_theme"
          )

          SettingsRowDivider()

          SettingsItemRow(
            icon = Icons.Outlined.Tune,
            title = "Dashboard Layout",
            value = null,
            onClick = {},
            testTag = "settings_item_layout"
          )
        }
      }

      // Group 3: SUPPORT
      item {
        SettingsGroupSection(title = "SUPPORT") {
          SettingsItemRow(
            icon = Icons.Outlined.Shield,
            title = "Privacy & Security",
            value = null,
            onClick = {},
            testTag = "settings_item_privacy"
          )

          SettingsRowDivider()

          SettingsItemRow(
            icon = Icons.Outlined.Info,
            title = "About DayFlow",
            value = "v2.4.1",
            onClick = {},
            testTag = "settings_item_about"
          )
        }
      }

      // Sign Out Button
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Sign Out",
            style = MaterialTheme.typography.bodyMedium.copy(
              fontSize = 15.sp,
              fontWeight = FontWeight.Medium
            ),
            color = DayFlowErrorRed,
            modifier = Modifier
              .clickable { }
              .padding(horizontal = 16.dp, vertical = 8.dp)
              .testTag("settings_sign_out")
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(48.dp))
      }
    }
  }
}

@Composable
private fun SettingsGroupSection(
  title: String,
  content: @Composable () -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.2.sp
      ),
      color = DayFlowPrimary,
      modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
    )

    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      color = DayFlowSurface,
      border = BorderStroke(1.dp, DayFlowSurfaceVariant)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        content()
      }
    }
  }
}

@Composable
private fun SettingsItemRow(
  icon: ImageVector,
  title: String,
  value: String?,
  onClick: () -> Unit,
  testTag: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 18.dp, vertical = 16.dp)
      .testTag(testTag),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = DayFlowTertiary,
        modifier = Modifier.size(22.dp)
      )

      Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge.copy(
          fontSize = 16.sp,
          fontWeight = FontWeight.Normal
        ),
        color = DayFlowOnSurface
      )
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      if (value != null) {
        Text(
          text = value,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
          color = DayFlowOnSurfaceVariant
        )
      }
      Icon(
        imageVector = Icons.Default.ChevronRight,
        contentDescription = null,
        tint = DayFlowOutlineVariant,
        modifier = Modifier.size(18.dp)
      )
    }
  }
}

@Composable
private fun SettingsRowDivider() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 56.dp)
      .height(1.dp)
      .background(DayFlowSurfaceVariant.copy(alpha = 0.6f))
  )
}
