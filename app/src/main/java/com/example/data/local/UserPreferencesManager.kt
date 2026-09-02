package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.model.CustomCategory
import com.example.ui.theme.DayFlowAccent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
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

enum class AiLanguage(val displayName: String, val instruction: String) {
  AUTO(
    "Auto",
    "Detect the user's language and style naturally. If user writes in English, reply in English. If user writes in Hindi or conversational Hinglish, reply naturally in matching Hindi/Hinglish without awkward or forced translation."
  ),
  ENGLISH(
    "English",
    "Respond in clear, natural, mindful English."
  ),
  HINDI(
    "Hindi",
    "Respond in natural Hindi (using clear Hindi or conversational Hinglish as natural for daily productivity)."
  );

  companion object {
    fun fromName(name: String?): AiLanguage {
      return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: AUTO
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

  private val _accentColor = MutableStateFlow(loadAccentColor())
  val accentColor: StateFlow<DayFlowAccent> = _accentColor.asStateFlow()

  private val _notifications = MutableStateFlow(loadNotificationPreferences())
  val notifications: StateFlow<NotificationPreferences> = _notifications.asStateFlow()

  private val _customCategories = MutableStateFlow(loadCustomCategories())
  val customCategories: StateFlow<List<CustomCategory>> = _customCategories.asStateFlow()

  private val _geminiApiKey = MutableStateFlow(loadGeminiApiKey())
  val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

  private val _geminiConnectionVerified = MutableStateFlow(loadGeminiConnectionVerified())
  val geminiConnectionVerified: StateFlow<Boolean> = _geminiConnectionVerified.asStateFlow()

  private val _aiLanguage = MutableStateFlow(loadAiLanguage())
  val aiLanguage: StateFlow<AiLanguage> = _aiLanguage.asStateFlow()

  private fun loadGeminiApiKey(): String {
    return prefs.getString(KEY_GEMINI_API_KEY, "")?.trim().orEmpty()
  }

  private fun loadGeminiConnectionVerified(): Boolean {
    return prefs.getBoolean(KEY_GEMINI_CONNECTION_VERIFIED, false)
  }

  private fun loadAiLanguage(): AiLanguage {
    val saved = prefs.getString(KEY_AI_LANGUAGE, AiLanguage.AUTO.name)
    return AiLanguage.fromName(saved)
  }

  private fun loadThemeMode(): AppThemeMode {
    val saved = prefs.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.name)
    return AppThemeMode.fromName(saved)
  }

  private fun loadAccentColor(): DayFlowAccent {
    val saved = prefs.getString(KEY_ACCENT_COLOR, DayFlowAccent.ROSEWOOD.id)
    return DayFlowAccent.fromId(saved)
  }

  private fun loadNotificationPreferences(): NotificationPreferences {
    return NotificationPreferences(
      isEnabled = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true),
      morningBriefing = prefs.getBoolean(KEY_MORNING_BRIEFING, true),
      eveningReview = prefs.getBoolean(KEY_EVENING_REVIEW, true),
      habitReminders = prefs.getBoolean(KEY_HABIT_REMINDERS, true)
    )
  }

  private fun loadCustomCategories(): List<CustomCategory> {
    val raw = prefs.getString(KEY_CUSTOM_CATEGORIES, "[]") ?: "[]"
    val list = mutableListOf<CustomCategory>()
    try {
      val jsonArray = JSONArray(raw)
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(
          CustomCategory(
            id = obj.getString("id"),
            name = obj.getString("name"),
            iconName = obj.optString("iconName", "category"),
            colorHex = obj.optLong("colorHex", 0xFF3B82F6)
          )
        )
      }
    } catch (_: Exception) {
      // Fallback
    }
    return list
  }

  fun saveCustomCategory(category: CustomCategory) {
    val current = _customCategories.value.toMutableList()
    val index = current.indexOfFirst { it.id == category.id || it.name.equals(category.name, ignoreCase = true) }
    if (index >= 0) {
      current[index] = category
    } else {
      current.add(category)
    }
    persistCustomCategories(current)
  }

  fun deleteCustomCategory(categoryId: String) {
    val current = _customCategories.value.filter { it.id != categoryId }
    persistCustomCategories(current)
  }

  private fun persistCustomCategories(categories: List<CustomCategory>) {
    val jsonArray = JSONArray()
    categories.forEach { cat ->
      val obj = JSONObject()
      obj.put("id", cat.id)
      obj.put("name", cat.name)
      obj.put("iconName", cat.iconName)
      obj.put("colorHex", cat.colorHex)
      jsonArray.put(obj)
    }
    prefs.edit().putString(KEY_CUSTOM_CATEGORIES, jsonArray.toString()).apply()
    _customCategories.value = categories
  }

  private val _manualStreakAdjustment = MutableStateFlow(loadManualStreakAdjustment())
  val manualStreakAdjustment: StateFlow<Int> = _manualStreakAdjustment.asStateFlow()

  private fun loadManualStreakAdjustment(): Int {
    return prefs.getInt(KEY_MANUAL_STREAK_ADJUSTMENT, 0)
  }

  fun setManualStreakAdjustment(adjustment: Int) {
    _manualStreakAdjustment.value = adjustment
    prefs.edit().putInt(KEY_MANUAL_STREAK_ADJUSTMENT, adjustment).apply()
  }

  fun addManualStreakDay() {
    val updated = _manualStreakAdjustment.value + 1
    setManualStreakAdjustment(updated)
  }

  fun removeManualStreakDay() {
    val updated = _manualStreakAdjustment.value - 1
    setManualStreakAdjustment(updated)
  }

  fun resetManualStreak(baseCalculatedStreak: Int) {
    setManualStreakAdjustment(-baseCalculatedStreak)
  }

  fun setThemeMode(mode: AppThemeMode) {
    prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    _themeMode.value = mode
  }

  fun setAccentColor(accent: DayFlowAccent) {
    prefs.edit().putString(KEY_ACCENT_COLOR, accent.id).apply()
    _accentColor.value = accent
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

  fun setGeminiApiKey(key: String) {
    val trimmed = key.trim()
    val isChanged = trimmed != _geminiApiKey.value
    prefs.edit()
      .putString(KEY_GEMINI_API_KEY, trimmed)
      .putBoolean(KEY_GEMINI_CONNECTION_VERIFIED, false)
      .apply()
    _geminiApiKey.value = trimmed
    if (isChanged) {
      _geminiConnectionVerified.value = false
    }
  }

  fun setGeminiConnectionVerified(verified: Boolean) {
    prefs.edit().putBoolean(KEY_GEMINI_CONNECTION_VERIFIED, verified).apply()
    _geminiConnectionVerified.value = verified
  }

  fun clearGeminiApiKey() {
    prefs.edit()
      .remove(KEY_GEMINI_API_KEY)
      .putBoolean(KEY_GEMINI_CONNECTION_VERIFIED, false)
      .apply()
    _geminiApiKey.value = ""
    _geminiConnectionVerified.value = false
  }

  fun setAiLanguage(language: AiLanguage) {
    prefs.edit().putString(KEY_AI_LANGUAGE, language.name).apply()
    _aiLanguage.value = language
  }

  fun exportToJson(): JSONObject {
    val json = JSONObject()
    json.put("themeMode", _themeMode.value.name)
    json.put("accentColor", _accentColor.value.id)
    json.put("aiLanguage", _aiLanguage.value.name)
    val notifObj = JSONObject()
    notifObj.put("isEnabled", _notifications.value.isEnabled)
    notifObj.put("morningBriefing", _notifications.value.morningBriefing)
    notifObj.put("eveningReview", _notifications.value.eveningReview)
    notifObj.put("habitReminders", _notifications.value.habitReminders)
    json.put("notifications", notifObj)

    val catArray = JSONArray()
    _customCategories.value.forEach { cat ->
      val c = JSONObject()
      c.put("id", cat.id)
      c.put("name", cat.name)
      c.put("iconName", cat.iconName)
      c.put("colorHex", cat.colorHex)
      catArray.put(c)
    }
    json.put("customCategories", catArray)
    return json
  }

  fun importFromJson(json: JSONObject) {
    if (json.has("themeMode")) {
      val modeName = json.optString("themeMode")
      setThemeMode(AppThemeMode.fromName(modeName))
    }
    if (json.has("accentColor")) {
      val accentId = json.optString("accentColor")
      setAccentColor(DayFlowAccent.fromId(accentId))
    }
    if (json.has("aiLanguage")) {
      val langName = json.optString("aiLanguage")
      setAiLanguage(AiLanguage.fromName(langName))
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
    if (json.has("customCategories")) {
      val catArray = json.optJSONArray("customCategories")
      if (catArray != null) {
        val list = mutableListOf<CustomCategory>()
        for (i in 0 until catArray.length()) {
          val obj = catArray.getJSONObject(i)
          list.add(
            CustomCategory(
              id = obj.optString("id"),
              name = obj.optString("name"),
              iconName = obj.optString("iconName", "category"),
              colorHex = obj.optLong("colorHex", 0xFF3B82F6)
            )
          )
        }
        persistCustomCategories(list)
      }
    }
  }

  companion object {
    private const val KEY_THEME_MODE = "key_theme_mode"
    private const val KEY_ACCENT_COLOR = "key_accent_color"
    private const val KEY_NOTIFICATIONS_ENABLED = "key_notifications_enabled"
    private const val KEY_MORNING_BRIEFING = "key_morning_briefing"
    private const val KEY_EVENING_REVIEW = "key_evening_review"
    private const val KEY_HABIT_REMINDERS = "key_habit_reminders"
    private const val KEY_CUSTOM_CATEGORIES = "key_custom_categories"
    private const val KEY_GEMINI_API_KEY = "key_gemini_api_key"
    private const val KEY_GEMINI_CONNECTION_VERIFIED = "key_gemini_connection_verified"
    private const val KEY_AI_LANGUAGE = "key_ai_language"
    private const val KEY_MANUAL_STREAK_ADJUSTMENT = "key_manual_streak_adjustment"
  }
}

