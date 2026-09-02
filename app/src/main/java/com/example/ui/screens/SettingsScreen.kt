package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.data.local.AppThemeMode
import com.example.data.local.NotificationPreferences
import com.example.ui.theme.DayFlowAccent
import com.example.ui.theme.DayFlowBackground
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOnPrimary
import com.example.ui.theme.DayFlowOnSurface
import com.example.ui.theme.DayFlowOnSurfaceVariant
import com.example.ui.theme.DayFlowOutlineVariant
import com.example.ui.theme.DayFlowSecondary
import com.example.ui.theme.DayFlowSurface
import com.example.ui.theme.DayFlowSurfaceContainerLow
import com.example.ui.theme.DayFlowSurfaceVariant
import com.example.ui.theme.DayFlowTertiary
import com.example.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  themeMode: AppThemeMode = AppThemeMode.SYSTEM,
  onThemeModeChange: (AppThemeMode) -> Unit = {},
  accentColor: DayFlowAccent = DayFlowAccent.ROSEWOOD,
  onOpenAccentColor: () -> Unit = {},
  notifications: NotificationPreferences = NotificationPreferences(),
  onNotificationsEnabledChange: (Boolean) -> Unit = {},
  onMorningBriefingChange: (Boolean) -> Unit = {},
  onEveningReviewChange: (Boolean) -> Unit = {},
  onHabitRemindersChange: (Boolean) -> Unit = {},
  onExportBackup: suspend () -> String = { "" },
  onImportBackup: suspend (String) -> Result<com.example.data.local.ImportResultSummary> = { Result.failure(Exception()) },
  onNavigateBack: () -> Unit = {}
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  var showThemeDialog by remember { mutableStateOf(false) }
  var showNotificationsSheet by remember { mutableStateOf(false) }
  var showPrivacySheet by remember { mutableStateOf(false) }
  var showAboutSheet by remember { mutableStateOf(false) }

  // Import Confirmation Dialog state
  var pendingImportJson by remember { mutableStateOf<String?>(null) }
  var showImportConfirmDialog by remember { mutableStateOf(false) }

  // File picker for Export
  val exportLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.CreateDocument("application/json")
  ) { uri: Uri? ->
    if (uri != null) {
      coroutineScope.launch {
        try {
          val json = onExportBackup()
          withContext(Dispatchers.IO) {
            context.contentResolver.openOutputStream(uri)?.use { stream ->
              stream.write(json.toByteArray())
            }
          }
          snackbarHostState.showSnackbar("Backup successfully exported!")
        } catch (e: Exception) {
          snackbarHostState.showSnackbar("Export failed: ${e.message}")
        }
      }
    }
  }

  // File picker for Import
  val importLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    if (uri != null) {
      coroutineScope.launch {
        try {
          val json = withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use { stream ->
              stream.bufferedReader().readText()
            }
          }
          if (!json.isNullOrBlank()) {
            pendingImportJson = json
            showImportConfirmDialog = true
          } else {
            snackbarHostState.showSnackbar("Selected file was empty.")
          }
        } catch (e: Exception) {
          snackbarHostState.showSnackbar("Could not read file: ${e.message}")
        }
      }
    }
  }

  Scaffold(
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    containerColor = DayFlowBackground,
    modifier = Modifier
      .fillMaxSize()
      .testTag("settings_screen")
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // 1. Top Bar
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

        Box(modifier = Modifier.size(40.dp))
      }

      // 2. Settings Content
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
      ) {
        // Group 1: PREFERENCES
        item {
          SettingsGroupSection(title = "PREFERENCES") {
            SettingsItemRow(
              icon = Icons.Outlined.Notifications,
              title = "Notifications",
              value = if (notifications.isEnabled) "Enabled" else "Off",
              onClick = { showNotificationsSheet = true },
              testTag = "settings_item_notifications"
            )

            SettingsRowDivider()

            SettingsItemRow(
              icon = Icons.Outlined.Palette,
              title = "Theme",
              value = themeMode.displayName,
              onClick = { showThemeDialog = true },
              testTag = "settings_item_theme"
            )

            SettingsRowDivider()

            SettingsItemRow(
              icon = Icons.Outlined.Palette,
              title = "Accent Color",
              value = accentColor.displayName,
              onClick = onOpenAccentColor,
              testTag = "settings_item_accent_color"
            )
          }
        }

        // Group 2: DATA & BACKUP
        item {
          SettingsGroupSection(title = "DATA & BACKUP") {
            SettingsItemRow(
              icon = Icons.Outlined.CloudUpload,
              title = "Export Data",
              value = "JSON file",
              onClick = {
                val defaultFileName = "DayFlow_Backup_${DateUtils.getTodayDateKey()}.json"
                exportLauncher.launch(defaultFileName)
              },
              testTag = "settings_item_export"
            )

            SettingsRowDivider()

            SettingsItemRow(
              icon = Icons.Outlined.CloudDownload,
              title = "Import Data",
              value = "Restore from file",
              onClick = {
                importLauncher.launch("application/json")
              },
              testTag = "settings_item_import"
            )
          }
        }

        // Group 3: SUPPORT & INFORMATION
        item {
          SettingsGroupSection(title = "SUPPORT & PRIVACY") {
            SettingsItemRow(
              icon = Icons.Outlined.Shield,
              title = "Privacy & Security",
              value = "Local first",
              onClick = { showPrivacySheet = true },
              testTag = "settings_item_privacy"
            )

            SettingsRowDivider()

            SettingsItemRow(
              icon = Icons.Outlined.Info,
              title = "About DayFlow",
              value = "v${BuildConfig.VERSION_NAME}",
              onClick = { showAboutSheet = true },
              testTag = "settings_item_about"
            )
          }
        }

        item {
          Spacer(modifier = Modifier.height(48.dp))
        }
      }
    }
  }

  // --- Theme Selection Dialog ---
  if (showThemeDialog) {
    AlertDialog(
      onDismissRequest = { showThemeDialog = false },
      title = {
        Text(
          text = "Choose Theme",
          style = MaterialTheme.typography.titleMedium,
          color = DayFlowOnSurface
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          AppThemeMode.entries.forEach { mode ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  onThemeModeChange(mode)
                  showThemeDialog = false
                }
                .padding(vertical = 8.dp, horizontal = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = themeMode == mode,
                onClick = {
                  onThemeModeChange(mode)
                  showThemeDialog = false
                },
                colors = RadioButtonDefaults.colors(
                  selectedColor = MaterialTheme.colorScheme.primary,
                  unselectedColor = DayFlowOnSurfaceVariant
                )
              )
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = mode.displayName,
                style = MaterialTheme.typography.bodyLarge,
                color = DayFlowOnSurface
              )
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showThemeDialog = false }) {
          Text("Cancel", color = MaterialTheme.colorScheme.primary)
        }
      },
      containerColor = DayFlowSurface,
      shape = RoundedCornerShape(16.dp)
    )
  }

  // --- Import Confirmation Dialog ---
  if (showImportConfirmDialog && pendingImportJson != null) {
    AlertDialog(
      onDismissRequest = {
        showImportConfirmDialog = false
        pendingImportJson = null
      },
      title = {
        Text(
          text = "Restore DayFlow Data?",
          style = MaterialTheme.typography.titleMedium,
          color = DayFlowOnSurface
        )
      },
      text = {
        Text(
          text = "Restoring this backup will replace your current tasks, habits, and goals with the data from the backup file.\n\nAre you sure you want to proceed?",
          style = MaterialTheme.typography.bodyMedium,
          color = DayFlowOnSurfaceVariant
        )
      },
      confirmButton = {
        Button(
          onClick = {
            val jsonToRestore = pendingImportJson
            showImportConfirmDialog = false
            pendingImportJson = null
            if (jsonToRestore != null) {
              coroutineScope.launch {
                val result = onImportBackup(jsonToRestore)
                if (result.isSuccess) {
                  val summary = result.getOrNull()
                  snackbarHostState.showSnackbar(summary?.message ?: "Data successfully restored!")
                } else {
                  val error = result.exceptionOrNull()?.message ?: "Failed to restore data."
                  snackbarHostState.showSnackbar("Import failed: $error")
                }
              }
            }
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = DayFlowOnPrimary
          )
        ) {
          Text("Restore")
        }
      },
      dismissButton = {
        TextButton(onClick = {
          showImportConfirmDialog = false
          pendingImportJson = null
        }) {
          Text("Cancel", color = DayFlowOnSurfaceVariant)
        }
      },
      containerColor = DayFlowSurface,
      shape = RoundedCornerShape(16.dp)
    )
  }

  // --- Notifications Bottom Sheet ---
  if (showNotificationsSheet) {
    ModalBottomSheet(
      onDismissRequest = { showNotificationsSheet = false },
      sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
      containerColor = DayFlowSurface,
      shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 12.dp)
      ) {
        Text(
          text = "Notification Preferences",
          style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
          color = DayFlowOnSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        NotificationToggleRow(
          title = "Allow Notifications",
          subtitle = "Master switch for all DayFlow reminders",
          checked = notifications.isEnabled,
          onCheckedChange = onNotificationsEnabledChange
        )

        SettingsRowDivider()

        NotificationToggleRow(
          title = "Daily Morning Briefing",
          subtitle = "Gentle morning overview at 08:00 AM",
          checked = notifications.morningBriefing && notifications.isEnabled,
          enabled = notifications.isEnabled,
          onCheckedChange = onMorningBriefingChange
        )

        SettingsRowDivider()

        NotificationToggleRow(
          title = "Evening Reflection",
          subtitle = "Mindful review of today's progress at 09:00 PM",
          checked = notifications.eveningReview && notifications.isEnabled,
          enabled = notifications.isEnabled,
          onCheckedChange = onEveningReviewChange
        )

        SettingsRowDivider()

        NotificationToggleRow(
          title = "Habit Reminders",
          subtitle = "Timely alerts for configured habit schedule times",
          checked = notifications.habitReminders && notifications.isEnabled,
          enabled = notifications.isEnabled,
          onCheckedChange = onHabitRemindersChange
        )

        Spacer(modifier = Modifier.height(32.dp))
      }
    }
  }

  // --- Privacy & Security Sheet ---
  if (showPrivacySheet) {
    ModalBottomSheet(
      onDismissRequest = { showPrivacySheet = false },
      sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
      containerColor = DayFlowSurface,
      shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 12.dp)
      ) {
        Text(
          text = "Privacy & Security",
          style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
          color = DayFlowOnSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrivacyPoint(
          title = "100% Local-First Storage",
          description = "All tasks, habits, daily reflections, and goal milestones are stored strictly in your device's local Room database."
        )

        Spacer(modifier = Modifier.height(12.dp))

        PrivacyPoint(
          title = "User-Controlled JSON Backups",
          description = "Exporting data generates a standard JSON file saved directly to your local file system. DayFlow never transmits backups to third-party servers."
        )

        Spacer(modifier = Modifier.height(12.dp))

        PrivacyPoint(
          title = "Mindful AI Summarization",
          description = "When interacting with the AI Coach, only high-level summary counts (such as total tasks completed and streak days) are processed. Raw database records and private notes are never sent."
        )

        Spacer(modifier = Modifier.height(12.dp))

        PrivacyPoint(
          title = "No Accounts & No Tracking",
          description = "DayFlow contains no third-party telemetry, advertising SDKs, or background tracking services."
        )

        Spacer(modifier = Modifier.height(32.dp))
      }
    }
  }

  // --- About DayFlow Sheet ---
  if (showAboutSheet) {
    ModalBottomSheet(
      onDismissRequest = { showAboutSheet = false },
      sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
      containerColor = DayFlowSurface,
      shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "DayFlow",
          style = MaterialTheme.typography.titleLarge.copy(
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
          ),
          color = DayFlowOnSurface
        )

        Text(
          text = "Version ${BuildConfig.VERSION_NAME}",
          style = MaterialTheme.typography.bodySmall,
          color = DayFlowOnSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "A mindful daily planner and habit companion built with modern Android Jetpack Compose and local-first architecture.",
          style = MaterialTheme.typography.bodyMedium.copy(
            lineHeight = 22.sp
          ),
          color = DayFlowOnSurfaceVariant,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center,
          modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = DayFlowSurfaceContainerLow,
          border = BorderStroke(1.dp, DayFlowOutlineVariant.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Crafted with Intentionality",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
              color = DayFlowOnSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Kotlin • Jetpack Compose • Room Database • Material 3",
              style = MaterialTheme.typography.bodySmall,
              color = DayFlowTertiary
            )
          }
        }

        Spacer(modifier = Modifier.height(32.dp))
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
      color = MaterialTheme.colorScheme.primary,
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

@Composable
private fun NotificationToggleRow(
  title: String,
  subtitle: String,
  checked: Boolean,
  enabled: Boolean = true,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontSize = 15.sp,
          fontWeight = FontWeight.Medium
        ),
        color = if (enabled) DayFlowOnSurface else DayFlowOnSurfaceVariant.copy(alpha = 0.5f)
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
        color = if (enabled) DayFlowOnSurfaceVariant else DayFlowOnSurfaceVariant.copy(alpha = 0.4f)
      )
    }

    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      enabled = enabled,
      colors = SwitchDefaults.colors(
        checkedThumbColor = DayFlowOnPrimary,
        checkedTrackColor = MaterialTheme.colorScheme.primary,
        uncheckedThumbColor = DayFlowOnSurfaceVariant,
        uncheckedTrackColor = DayFlowSurfaceVariant
      )
    )
  }
}

@Composable
private fun PrivacyPoint(
  title: String,
  description: String
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = title,
      style = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold
      ),
      color = DayFlowOnSurface
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = description,
      style = MaterialTheme.typography.bodySmall.copy(
        fontSize = 13.sp,
        lineHeight = 18.sp
      ),
      color = DayFlowOnSurfaceVariant
    )
  }
}
