package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

enum class AppThemeMode(val displayName: String) {
  SYSTEM("System default"),
  LIGHT("Light"),
  DARK("Dark");

  companion object {
    fun fromName(name: String?): AppThemeMode {
      return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: SYSTEM
    }
  }
}

data class NotificationPreferences(
  val isEnabled: Boolean = true,
  val morningBriefing: Boolean = true,
  val eveningReview: Boolean = true,
  val habitReminders: Boolean = true
)

class UserPreferencesManager(context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("dayflow_preferences", Context.MODE_PRIVATE)

  private val _themeMode = MutableStateFlow(loadThemeMode())
  val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

  private val _notifications = MutableStateFlow(loadNotificationPreferences())
  val notifications: StateFlow<NotificationPreferences> = _notifications.asStateFlow()

  private fun loadThemeMode(): AppThemeMode {
    val saved = prefs.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.name)
    return AppThemeMode.fromName(saved)
  }

  private fun loadNotificationPreferences(): NotificationPreferences {
    return NotificationPreferences(
      isEnabled = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true),
      morningBriefing = prefs.getBoolean(KEY_MORNING_BRIEFING, true),
      eveningReview = prefs.getBoolean(KEY_EVENING_REVIEW, true),
      habitReminders = prefs.getBoolean(KEY_HABIT_REMINDERS, true)
    )
  }

  fun setThemeMode(mode: AppThemeMode) {
    prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    _themeMode.value = mode
  }

  fun setNotificationsEnabled(enabled: Boolean) {
    prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    _notifications.value = _notifications.value.copy(isEnabled = enabled)
  }

  fun setMorningBriefing(enabled: Boolean) {
    prefs.edit().putBoolean(KEY_MORNING_BRIEFING, enabled).apply()
    _notifications.value = _notifications.value.copy(morningBriefing = enabled)
  }

  fun setEveningReview(enabled: Boolean) {
    prefs.edit().putBoolean(KEY_EVENING_REVIEW, enabled).apply()
    _notifications.value = _notifications.value.copy(eveningReview = enabled)
  }

  fun setHabitReminders(enabled: Boolean) {
    prefs.edit().putBoolean(KEY_HABIT_REMINDERS, enabled).apply()
    _notifications.value = _notifications.value.copy(habitReminders = enabled)
  }

  fun exportToJson(): JSONObject {
    val json = JSONObject()
    json.put("themeMode", _themeMode.value.name)
    val notifObj = JSONObject()
    notifObj.put("isEnabled", _notifications.value.isEnabled)
    notifObj.put("morningBriefing", _notifications.value.morningBriefing)
    notifObj.put("eveningReview", _notifications.value.eveningReview)
    notifObj.put("habitReminders", _notifications.value.habitReminders)
    json.put("notifications", notifObj)
    return json
  }

  fun importFromJson(json: JSONObject) {
    if (json.has("themeMode")) {
      val modeName = json.optString("themeMode")
      setThemeMode(AppThemeMode.fromName(modeName))
    }
    if (json.has("notifications")) {
      val notifObj = json.optJSONObject("notifications")
      if (notifObj != null) {
        val enabled = notifObj.optBoolean("isEnabled", true)
        val morning = notifObj.optBoolean("morningBriefing", true)
        val evening = notifObj.optBoolean("eveningReview", true)
        val habits = notifObj.optBoolean("habitReminders", true)
        setNotificationsEnabled(enabled)
        setMorningBriefing(morning)
        setEveningReview(evening)
        setHabitReminders(habits)
      }
    }
  }

  companion object {
    private const val KEY_THEME_MODE = "key_theme_mode"
    private const val KEY_NOTIFICATIONS_ENABLED = "key_notifications_enabled"
    private const val KEY_MORNING_BRIEFING = "key_morning_briefing"
    private const val KEY_EVENING_REVIEW = "key_evening_review"
    private const val KEY_HABIT_REMINDERS = "key_habit_reminders"
  }
}
