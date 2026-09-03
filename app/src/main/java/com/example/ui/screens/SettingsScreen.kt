package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.data.ai.ConnectionTestResult
import com.example.data.local.AiLanguage
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
import com.example.ui.theme.LocalDayFlowIsDark
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
  geminiApiKey: String = "",
  isGeminiVerified: Boolean = false,
  onSaveGeminiApiKey: (String) -> Unit = {},
  onClearGeminiApiKey: () -> Unit = {},
  aiLanguage: AiLanguage = AiLanguage.AUTO,
  onAiLanguageChange: (AiLanguage) -> Unit = {},
  onTestGeminiConnection: (String?) -> Unit = {},
  isTestingConnection: Boolean = false,
  testConnectionResult: ConnectionTestResult? = null,
  onClearTestConnectionResult: () -> Unit = {},
  aiMemories: List<com.example.model.AiMemory> = emptyList(),
  onDeleteMemory: (String) -> Unit = {},
  onClearAllMemories: () -> Unit = {},
  onClearAllChatHistory: () -> Unit = {},
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
  var showGeminiConfigSheet by remember { mutableStateOf(false) }
  var showLanguageDialog by remember { mutableStateOf(false) }

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
              stream.bufferedReader().use { it.readText() }
            }
          }
          if (json != null && json.isNotBlank()) {
            pendingImportJson = json
            showImportConfirmDialog = true
          } else {
            snackbarHostState.showSnackbar("Selected file was empty or unreadable.")
          }
        } catch (e: Exception) {
          snackbarHostState.showSnackbar("Failed to read backup file: ${e.message}")
        }
      }
    }
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(DayFlowBackground),
    containerColor = DayFlowBackground,
    snackbarHost = { SnackbarHost(snackbarHostState) }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .testTag("settings_screen")
    ) {
      // 1. Settings Top Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        IconButton(
          onClick = onNavigateBack,
          modifier = Modifier.testTag("settings_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = DayFlowOnSurface
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
        // Group 1: AI & GEMINI COACH
        item {
          SettingsGroupSection(title = "AI COACH & GEMINI") {
            val geminiStatusText = when {
              geminiApiKey.isBlank() -> "Not Configured"
              isGeminiVerified -> "Connected & Verified"
              else -> "Configured (Unverified)"
            }

            SettingsItemRow(
              icon = Icons.Outlined.AutoAwesome,
              title = "Gemini AI Coach",
              value = geminiStatusText,
              onClick = {
                onClearTestConnectionResult()
                showGeminiConfigSheet = true
              },
              testTag = "settings_item_gemini"
            )

            SettingsRowDivider()

            SettingsItemRow(
              icon = Icons.Outlined.Language,
              title = "Coach Language",
              value = aiLanguage.displayName,
              onClick = { showLanguageDialog = true },
              testTag = "settings_item_language"
            )

            SettingsRowDivider()

            var showMemorySheet by remember { mutableStateOf(false) }
            SettingsItemRow(
              icon = Icons.Outlined.Psychology,
              title = "AI Memory",
              value = "${aiMemories.size} Saved Facts",
              onClick = { showMemorySheet = true },
              testTag = "settings_item_memory"
            )
            
            if (showMemorySheet) {
              AiMemorySheet(
                memories = aiMemories,
                onDismiss = { showMemorySheet = false },
                onDeleteMemory = onDeleteMemory,
                onClearAllMemories = onClearAllMemories
              )
            }

            SettingsRowDivider()

            var showHistorySheet by remember { mutableStateOf(false) }
            SettingsItemRow(
              icon = Icons.Outlined.History,
              title = "Chat History",
              value = "Manage Chats",
              onClick = { showHistorySheet = true },
              testTag = "settings_item_chat_history"
            )
            
            if (showHistorySheet) {
              ChatHistoryManagementSheet(
                onDismiss = { showHistorySheet = false },
                onClearAllChatHistory = onClearAllChatHistory
              )
            }
          }
        }

        // Group 2: PREFERENCES
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

            val isDark = LocalDayFlowIsDark.current
            SettingsItemRow(
              icon = Icons.Outlined.Palette,
              title = "Accent Color",
              value = accentColor.getDisplayName(isDark),
              onClick = onOpenAccentColor,
              testTag = "settings_item_accent_color"
            )
          }
        }

        // Group 3: DATA & BACKUP
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

        // Group 4: SUPPORT & INFORMATION
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

  // --- Gemini AI Configuration Bottom Sheet ---
  if (showGeminiConfigSheet) {
    var apiKeyInput by remember { mutableStateOf(geminiApiKey) }
    var isKeyVisible by remember { mutableStateOf(false) }
    var showDisconnectConfirm by remember { mutableStateOf(false) }

    ModalBottomSheet(
      onDismissRequest = {
        showGeminiConfigSheet = false
        onClearTestConnectionResult()
      },
      sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
      containerColor = DayFlowSurface,
      shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 12.dp)
          .testTag("gemini_config_sheet")
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(
              text = "Gemini AI Integration",
              style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
              color = DayFlowOnSurface
            )
            Text(
              text = "Power mindful reflections and intelligent daily coaching",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
              color = DayFlowOnSurfaceVariant
            )
          }

          Surface(
            shape = CircleShape,
            color = if (geminiApiKey.isNotBlank()) DayFlowSecondary.copy(alpha = 0.15f) else DayFlowSurfaceContainerLow
          ) {
            Icon(
              imageVector = Icons.Filled.AutoAwesome,
              contentDescription = null,
              tint = if (geminiApiKey.isNotBlank()) DayFlowSecondary else DayFlowOnSurfaceVariant,
              modifier = Modifier
                .padding(8.dp)
                .size(20.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Connection Status Banner
        val isSuccessfullyVerified = testConnectionResult is ConnectionTestResult.Success || (geminiApiKey.isNotBlank() && isGeminiVerified && testConnectionResult == null)
        val isFailedState = testConnectionResult is ConnectionTestResult.InvalidKey ||
          testConnectionResult is ConnectionTestResult.QuotaExhausted ||
          testConnectionResult is ConnectionTestResult.Error

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = when {
            isSuccessfullyVerified -> Color(0xFFE8F5E9)
            isFailedState -> Color(0xFFFFEBEE)
            testConnectionResult is ConnectionTestResult.NoInternet ||
            testConnectionResult is ConnectionTestResult.Timeout -> Color(0xFFFFF3E0)
            geminiApiKey.isNotBlank() -> DayFlowTertiary.copy(alpha = 0.12f)
            else -> DayFlowSurfaceContainerLow
          },
          border = BorderStroke(
            1.dp,
            when {
              isSuccessfullyVerified -> Color(0xFF81C784)
              isFailedState -> Color(0xFFE57373)
              testConnectionResult is ConnectionTestResult.NoInternet ||
              testConnectionResult is ConnectionTestResult.Timeout -> Color(0xFFFFB74D)
              geminiApiKey.isNotBlank() -> DayFlowTertiary.copy(alpha = 0.3f)
              else -> DayFlowCardBorder
            }
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Icon(
              imageVector = when {
                isSuccessfullyVerified -> Icons.Default.CheckCircle
                testConnectionResult is ConnectionTestResult.NoInternet -> Icons.Default.WifiOff
                isFailedState || testConnectionResult is ConnectionTestResult.Timeout -> Icons.Default.ErrorOutline
                geminiApiKey.isNotBlank() -> Icons.Default.Key
                else -> Icons.Default.Key
              },
              contentDescription = null,
              tint = when {
                isSuccessfullyVerified -> Color(0xFF2E7D32)
                isFailedState -> Color(0xFFC62828)
                testConnectionResult is ConnectionTestResult.NoInternet ||
                testConnectionResult is ConnectionTestResult.Timeout -> Color(0xFFEF6C00)
                geminiApiKey.isNotBlank() -> DayFlowTertiary
                else -> DayFlowOnSurfaceVariant
              },
              modifier = Modifier.size(22.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = when {
                  testConnectionResult is ConnectionTestResult.Success -> "Connection Active & Verified"
                  geminiApiKey.isNotBlank() && isGeminiVerified && testConnectionResult == null -> "Connection Active & Verified"
                  testConnectionResult is ConnectionTestResult.InvalidKey -> "Invalid API Key"
                  testConnectionResult is ConnectionTestResult.NoInternet -> "Internet Unavailable"
                  testConnectionResult is ConnectionTestResult.QuotaExhausted -> "Quota / Rate Limit Exceeded"
                  testConnectionResult is ConnectionTestResult.Timeout -> "Request Timed Out"
                  testConnectionResult is ConnectionTestResult.Error -> "Connection Issue"
                  geminiApiKey.isNotBlank() -> "Gemini API Configured (Unverified)"
                  else -> "Gemini API Not Configured"
                },
                style = MaterialTheme.typography.titleSmall.copy(
                  fontSize = 14.sp,
                  fontWeight = FontWeight.SemiBold
                ),
                color = when {
                  isSuccessfullyVerified -> Color(0xFF1B5E20)
                  isFailedState -> Color(0xFFB71C1C)
                  testConnectionResult is ConnectionTestResult.NoInternet ||
                  testConnectionResult is ConnectionTestResult.Timeout -> Color(0xFFE65100)
                  geminiApiKey.isNotBlank() -> DayFlowOnSurface
                  else -> DayFlowOnSurface
                }
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = when (val res = testConnectionResult) {
                  is ConnectionTestResult.Success -> "Gemini 3.5 Flash model is ready to assist your productivity."
                  is ConnectionTestResult.InvalidKey -> res.message
                  is ConnectionTestResult.NoInternet -> res.message
                  is ConnectionTestResult.QuotaExhausted -> res.message
                  is ConnectionTestResult.Timeout -> res.message
                  is ConnectionTestResult.Error -> res.message
                  else -> when {
                    geminiApiKey.isNotBlank() && isGeminiVerified -> "Gemini 3.5 Flash is verified and active."
                    geminiApiKey.isNotBlank() -> "Key stored. Tap 'Test Connection' below to verify live access to Gemini 3.5 Flash."
                    else -> "Enter your API key below or use the mindful offline coach."
                  }
                },
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = DayFlowOnSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // API Key Input
        Text(
          text = "GEMINI API KEY",
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
          ),
          color = DayFlowOnSurfaceVariant
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
          value = apiKeyInput,
          onValueChange = {
            apiKeyInput = it
            onClearTestConnectionResult()
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("gemini_api_key_input"),
          placeholder = { Text("AIzaSy...", color = DayFlowOnSurfaceVariant.copy(alpha = 0.5f)) },
          singleLine = true,
          visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
          trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (apiKeyInput.isNotEmpty()) {
                IconButton(onClick = {
                  apiKeyInput = ""
                  onClearTestConnectionResult()
                }) {
                  Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear",
                    tint = DayFlowOnSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
              IconButton(onClick = { isKeyVisible = !isKeyVisible }) {
                Icon(
                  imageVector = if (isKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = if (isKeyVisible) "Hide Key" else "Show Key",
                  tint = DayFlowOnSurfaceVariant,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = DayFlowOutlineVariant,
            focusedContainerColor = DayFlowBackground,
            unfocusedContainerColor = DayFlowBackground
          ),
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Paste Helper Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .clickable {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = clipboard.primaryClip
                if (clip != null && clip.itemCount > 0) {
                  val text = clip.getItemAt(0).text?.toString()?.trim().orEmpty()
                  if (text.isNotEmpty()) {
                    apiKeyInput = text
                    onClearTestConnectionResult()
                    Toast.makeText(context, "Pasted from clipboard", Toast.LENGTH_SHORT).show()
                  }
                }
              }
              .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ContentPaste,
              contentDescription = "Paste",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "Paste from Clipboard",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              ),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = { onTestGeminiConnection(apiKeyInput) },
            enabled = apiKeyInput.isNotBlank() && !isTestingConnection,
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("gemini_test_connection_button"),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
          ) {
            if (isTestingConnection) {
              CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text("Testing...", fontSize = 13.sp)
            } else {
              Text("Test Connection", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
            }
          }

          Button(
            onClick = {
              onSaveGeminiApiKey(apiKeyInput)
              Toast.makeText(context, "API Key saved successfully", Toast.LENGTH_SHORT).show()
              showGeminiConfigSheet = false
            },
            enabled = apiKeyInput.isNotBlank(),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("gemini_save_key_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = DayFlowOnPrimary
            )
          ) {
            Text("Save Key", fontSize = 13.sp)
          }
        }

        // Disconnect Option
        if (geminiApiKey.isNotBlank()) {
          Spacer(modifier = Modifier.height(12.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
          ) {
            TextButton(
              onClick = { showDisconnectConfirm = true },
              modifier = Modifier.testTag("gemini_disconnect_button")
            ) {
              Text(
                text = "Disconnect / Remove API Key",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Privacy & Storage Guarantee
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = DayFlowSurfaceContainerLow,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = DayFlowOnSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
            Column {
              Text(
                text = "100% Private On-Device Storage",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold
                ),
                color = DayFlowOnSurface
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Your API key is saved locally in private app storage and sent directly from your device to Google's official Gemini endpoint. It is never transmitted through or stored on any intermediary servers.",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 11.sp,
                  lineHeight = 16.sp
                ),
                color = DayFlowOnSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
      }
    }

    // Disconnect Confirmation Dialog
    if (showDisconnectConfirm) {
      AlertDialog(
        onDismissRequest = { showDisconnectConfirm = false },
        title = {
          Text(
            text = "Disconnect Gemini?",
            style = MaterialTheme.typography.titleMedium,
            color = DayFlowOnSurface
          )
        },
        text = {
          Text(
            text = "This will remove your API key from this device. DayFlow will revert to the offline mindful coach until you enter an API key again.",
            style = MaterialTheme.typography.bodyMedium,
            color = DayFlowOnSurfaceVariant
          )
        },
        confirmButton = {
          Button(
            onClick = {
              onClearGeminiApiKey()
              apiKeyInput = ""
              showDisconnectConfirm = false
              showGeminiConfigSheet = false
              Toast.makeText(context, "Gemini API key removed", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.error,
              contentColor = Color.White
            )
          ) {
            Text("Disconnect")
          }
        },
        dismissButton = {
          TextButton(onClick = { showDisconnectConfirm = false }) {
            Text("Cancel", color = DayFlowOnSurfaceVariant)
          }
        },
        containerColor = DayFlowSurface,
        shape = RoundedCornerShape(16.dp)
      )
    }
  }

  // --- AI Coach Language Dialog ---
  if (showLanguageDialog) {
    AlertDialog(
      onDismissRequest = { showLanguageDialog = false },
      title = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Translate,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
          Text(
            text = "Coach Language",
            style = MaterialTheme.typography.titleMedium,
            color = DayFlowOnSurface
          )
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          AiLanguage.entries.forEach { lang ->
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (aiLanguage == lang) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent,
              border = BorderStroke(
                1.dp,
                if (aiLanguage == lang) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else DayFlowCardBorder
              ),
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  onAiLanguageChange(lang)
                  showLanguageDialog = false
                }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                RadioButton(
                  selected = aiLanguage == lang,
                  onClick = {
                    onAiLanguageChange(lang)
                    showLanguageDialog = false
                  },
                  colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = DayFlowOnSurfaceVariant
                  )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = when (lang) {
                      AiLanguage.AUTO -> "Auto (Match Language)"
                      AiLanguage.ENGLISH -> "English"
                      AiLanguage.HINDI -> "Hindi / Hinglish"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Medium
                    ),
                    color = DayFlowOnSurface
                  )
                  Text(
                    text = when (lang) {
                      AiLanguage.AUTO -> "Detects English, Hindi, and Hinglish automatically"
                      AiLanguage.ENGLISH -> "Responses in mindful, clear English"
                      AiLanguage.HINDI -> "Natural Hindi / conversational Hinglish"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = DayFlowOnSurfaceVariant
                  )
                }
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showLanguageDialog = false }) {
          Text("Done", color = MaterialTheme.colorScheme.primary)
        }
      },
      containerColor = DayFlowSurface,
      shape = RoundedCornerShape(16.dp)
    )
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
          title = "Enable Notifications",
          subtitle = "Allow DayFlow to send mindful reminders",
          checked = notifications.isEnabled,
          onCheckedChange = onNotificationsEnabledChange
        )

        SettingsRowDivider()

        NotificationToggleRow(
          title = "Morning Briefing",
          subtitle = "Receive daily focus plan at 8:00 AM",
          checked = notifications.morningBriefing,
          enabled = notifications.isEnabled,
          onCheckedChange = onMorningBriefingChange
        )

        SettingsRowDivider()

        NotificationToggleRow(
          title = "Evening Review",
          subtitle = "Reflect on completed goals at 9:00 PM",
          checked = notifications.eveningReview,
          enabled = notifications.isEnabled,
          onCheckedChange = onEveningReviewChange
        )

        SettingsRowDivider()

        NotificationToggleRow(
          title = "Habit Reminders",
          subtitle = "Smart nudge for pending daily habits",
          checked = notifications.habitReminders,
          enabled = notifications.isEnabled,
          onCheckedChange = onHabitRemindersChange
        )

        Spacer(modifier = Modifier.height(24.dp))
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
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.Shield,
            contentDescription = null,
            tint = DayFlowSecondary,
            modifier = Modifier.size(24.dp)
          )
          Text(
            text = "Privacy & Data Ownership",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
            color = DayFlowOnSurface
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        PrivacyPoint(
          title = "100% Local-First Architecture",
          description = "All tasks, habits, goals, categories, and calendar items are stored directly on your phone in an offline SQLite database."
        )

        Spacer(modifier = Modifier.height(12.dp))

        PrivacyPoint(
          title = "No Analytics or Tracking",
          description = "DayFlow does not collect telemetry, usage metrics, device IDs, or advertising trackers."
        )

        Spacer(modifier = Modifier.height(12.dp))

        PrivacyPoint(
          title = "Direct Gemini Integration",
          description = "When you configure a Gemini API key, calls travel directly between your device and Google's official endpoint with your structured schedule context. No middleman servers touch your requests."
        )

        Spacer(modifier = Modifier.height(12.dp))

        PrivacyPoint(
          title = "Complete Data Export & Portability",
          description = "You can export your complete DayFlow data in standard JSON format at any time from the Data & Backup menu."
        )

        Spacer(modifier = Modifier.height(24.dp))
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
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "DF",
            style = MaterialTheme.typography.titleLarge.copy(
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "DayFlow",
          style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
          color = DayFlowOnSurface
        )

        Text(
          text = "Version ${BuildConfig.VERSION_NAME}",
          style = MaterialTheme.typography.bodySmall,
          color = DayFlowOnSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "A calm, mindful daily productivity and habit tracker built with Android Jetpack Compose and local-first architecture.",
          style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 14.sp,
            lineHeight = 20.sp
          ),
          color = DayFlowOnSurfaceVariant,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center,
          modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))
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
      modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )

    Surface(
      shape = RoundedCornerShape(16.dp),
      color = DayFlowSurface,
      border = BorderStroke(1.dp, DayFlowCardBorder),
      modifier = Modifier.fillMaxWidth()
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
  value: String? = null,
  onClick: () -> Unit,
  testTag: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 16.dp, vertical = 14.dp)
      .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      modifier = Modifier.weight(1f)
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(DayFlowSurfaceContainerLow),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = DayFlowOnSurfaceVariant,
          modifier = Modifier.size(20.dp)
        )
      }

      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontSize = 15.sp,
          fontWeight = FontWeight.Medium
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiMemorySheet(
  memories: List<com.example.model.AiMemory>,
  onDismiss: () -> Unit,
  onDeleteMemory: (String) -> Unit,
  onClearAllMemories: () -> Unit
) {
  var showClearConfirm by remember { mutableStateOf(false) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    containerColor = DayFlowSurface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 32.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "AI Memory",
          style = MaterialTheme.typography.titleLarge,
          color = DayFlowOnSurface
        )
        if (memories.isNotEmpty()) {
          Text(
            text = "Clear All",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.clickable { showClearConfirm = true }.padding(8.dp)
          )
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Saved information that helps DayFlow AI give more personalized and relevant responses.",
        style = MaterialTheme.typography.bodyMedium,
        color = DayFlowOnSurfaceVariant
      )
      Spacer(modifier = Modifier.height(16.dp))

      if (memories.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
          Text("DayFlow hasn't saved any useful preferences yet.", color = DayFlowOnSurfaceVariant)
        }
      } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          items(memories.size, key = { memories[it].id }) { idx ->
            val mem = memories[idx]
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = DayFlowSurfaceContainerLow,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(text = mem.category.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                  Text(text = mem.text, style = MaterialTheme.typography.bodyMedium, color = DayFlowOnSurface)
                }
                IconButton(onClick = { onDeleteMemory(mem.id) }) {
                  Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Memory", tint = DayFlowOnSurfaceVariant)
                }
              }
            }
          }
        }
      }
    }
  }

  if (showClearConfirm) {
    AlertDialog(
      onDismissRequest = { showClearConfirm = false },
      title = { Text("Clear All AI Memory?") },
      text = { Text("This will permanently delete all personalized AI memories. Your DayFlow tasks and chat history will not be affected.") },
      confirmButton = {
        Button(
          onClick = {
            onClearAllMemories()
            showClearConfirm = false
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Clear All")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showClearConfirm = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHistoryManagementSheet(
  onDismiss: () -> Unit,
  onClearAllChatHistory: () -> Unit
) {
  var showClearConfirm by remember { mutableStateOf(false) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    containerColor = DayFlowSurface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 32.dp)
    ) {
      Text(
        text = "Chat History",
        style = MaterialTheme.typography.titleLarge,
        color = DayFlowOnSurface
      )
      Spacer(modifier = Modifier.height(16.dp))
      
      Button(
        onClick = { showClearConfirm = true },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
      ) {
        Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text("Clear All Chat History")
      }
      
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = "This will permanently delete all saved AI conversations. Your AI Memory and DayFlow data will remain safe.",
        style = MaterialTheme.typography.bodySmall,
        color = DayFlowOnSurfaceVariant
      )
    }
  }

  if (showClearConfirm) {
    AlertDialog(
      onDismissRequest = { showClearConfirm = false },
      title = { Text("Clear All Chat History?") },
      text = { Text("This cannot be undone. All saved chat sessions will be deleted.") },
      confirmButton = {
        Button(
          onClick = {
            onClearAllChatHistory()
            showClearConfirm = false
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Delete All Chats")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showClearConfirm = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
