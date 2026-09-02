package com.example.data.ai

import com.example.BuildConfig
import com.example.data.local.AiLanguage
import com.example.model.AiChatMessage
import com.example.model.CoachInsight
import com.example.model.DailyProgressSummary
import com.example.model.GoalItem
import com.example.model.HabitItem
import com.example.model.InsightType
import com.example.model.StatisticsData
import com.example.model.TaskItem
import com.example.model.TaskPriority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

enum class CoachActionType {
  DAILY_BRIEFING,
  DAY_REVIEW,
  GOAL_GUIDANCE,
  ASK_AI
}

enum class AiConnectionState {
  CONNECTED,
  NOT_CONFIGURED,
  OFFLINE,
  QUOTA_EXHAUSTED,
  ERROR
}

enum class CoachIntent {
  DAILY_BRIEFING,
  DAY_REVIEW,
  GOAL_GUIDANCE,
  HABIT_STREAK,
  STATISTICS_PRODUCTIVITY,
  TASK_PLANNING,
  GENERAL
}

sealed class ConnectionTestResult {
  object Success : ConnectionTestResult()
  data class InvalidKey(val message: String = "The API key appears to be invalid or revoked.") : ConnectionTestResult()
  data class NoInternet(val message: String = "Internet is unavailable. Check your network.") : ConnectionTestResult()
  data class QuotaExhausted(val message: String = "Gemini API quota or rate limit is exhausted.") : ConnectionTestResult()
  data class Timeout(val message: String = "Connection timed out. Please try again.") : ConnectionTestResult()
  data class Error(val message: String) : ConnectionTestResult()
}

/**
 * Encapsulates the live, dynamic DayFlow application state at the exact moment of coaching.
 */
data class AiCoachContext(
  val todayTasks: List<TaskItem>,
  val todayHabits: List<HabitItem>,
  val activeGoals: List<GoalItem>,
  val summary: DailyProgressSummary,
  val selectedDate: String,
  val statisticsData: StatisticsData? = null,
  val currentDateTimeString: String = "",
  val queryIntent: CoachIntent = CoachIntent.GENERAL,
  val memories: List<com.example.model.AiMemory> = emptyList()
)

object DayFlowContextBuilder {

  /**
   * Classifies user query to prioritize relevant metrics while keeping context compact and privacy-preserving.
   */
  fun detectIntent(query: String, explicitAction: CoachActionType? = null): CoachIntent {
    if (explicitAction != null) {
      return when (explicitAction) {
        CoachActionType.DAILY_BRIEFING -> CoachIntent.DAILY_BRIEFING
        CoachActionType.DAY_REVIEW -> CoachIntent.DAY_REVIEW
        CoachActionType.GOAL_GUIDANCE -> CoachIntent.GOAL_GUIDANCE
        CoachActionType.ASK_AI -> CoachIntent.GENERAL
      }
    }

    val lower = query.lowercase()
    return when {
      lower.contains("briefing") || lower.contains("morning") || lower.contains("plan today") ||
        lower.contains("shuru") || (lower.contains("aaj") && lower.contains("kya")) || lower.contains("what should i do") ||
        lower.contains("prioritize") || lower.contains("start with") || lower.contains("focus on today") -> CoachIntent.DAILY_BRIEFING

      lower.contains("review") || lower.contains("evening") || lower.contains("how did i do") ||
        lower.contains("how productive") || lower.contains("end of day") || lower.contains("kaisa raha") -> CoachIntent.DAY_REVIEW

      lower.contains("goal") || lower.contains("target") || lower.contains("milestone") ||
        lower.contains("lakshya") || lower.contains("portfolio") || lower.contains("deadline") -> CoachIntent.GOAL_GUIDANCE

      lower.contains("habit") || lower.contains("streak") || lower.contains("consistency") ||
        lower.contains("daily rhythm") || lower.contains("aadat") -> CoachIntent.HABIT_STREAK

      lower.contains("stat") || lower.contains("focus time") || lower.contains("completion rate") ||
        lower.contains("percentage") || lower.contains("metrics") -> CoachIntent.STATISTICS_PRODUCTIVITY

      lower.contains("task") || lower.contains("pending") || lower.contains("schedule") ||
        lower.contains("todo") -> CoachIntent.TASK_PLANNING

      else -> CoachIntent.GENERAL
    }
  }

  /**
   * Assembles a structured, minimal, and privacy-safe context payload for Gemini.
   * Excludes UUIDs, system keys, private IDs, and debug metadata.
   */
  fun buildStructuredContext(context: AiCoachContext, userQuery: String = ""): String {
    val totalTasks = context.todayTasks.size
    val completedTasks = context.todayTasks.count { it.isCompleted }
    val remainingTasks = context.todayTasks.filter { !it.isCompleted }
    val completedTasksList = context.todayTasks.filter { it.isCompleted }
    val totalPlannedMin = context.todayTasks.sumOf { it.estimatedMinutes }
    val completedFocusMin = completedTasksList.sumOf { it.estimatedMinutes }

    val totalHabits = context.todayHabits.size
    val completedHabits = context.todayHabits.count { it.completedToday }

    val activeGoalsList = context.activeGoals.filter { !it.isCompleted }
    val completedGoalsList = context.activeGoals.filter { it.isCompleted }

    val intent = if (context.queryIntent != CoachIntent.GENERAL) {
      context.queryIntent
    } else {
      detectIntent(userQuery)
    }

    return buildString {
      appendLine("=== LIVE DAYFLOW STATE ===")
      if (context.currentDateTimeString.isNotBlank()) {
        appendLine("Current Time: ${context.currentDateTimeString}")
      }
      appendLine("Target Date: ${context.selectedDate}")
      appendLine("Tasks Summary: $completedTasks of $totalTasks completed (Logged Focus: ${completedFocusMin}m / Total Planned: ${totalPlannedMin}m)")
      appendLine("Habits Summary: $completedHabits of $totalHabits completed today (Habit Streak: ${context.summary.currentStreak} days)")
      appendLine()

      // 1. SMART RELEVANCE FOCUS SECTION
      when (intent) {
        CoachIntent.DAILY_BRIEFING, CoachIntent.TASK_PLANNING -> {
          appendLine("[FOCUS: DAILY PLANNING & TIMELINE]")
          appendLine("Pending Tasks Remaining: ${remainingTasks.size} of $totalTasks")
          if (remainingTasks.isNotEmpty()) {
            appendLine("Upcoming Today Timeline:")
            remainingTasks.forEach { task ->
              val priorityTag = if (task.priority == TaskPriority.HIGH) " [HIGH PRIORITY]" else ""
              val timeInfo = if (!task.endTime.isNullOrBlank()) "${task.time} - ${task.endTime}" else task.time
              appendLine(" • \"${task.title}\" | Time: $timeInfo (${task.estimatedMinutes}m) | Category: ${task.category.displayName}$priorityTag")
            }
          } else {
            appendLine("All planned tasks for today are already completed.")
          }
          if (context.todayHabits.isNotEmpty()) {
            val pendingHabits = context.todayHabits.filter { !it.completedToday }
            appendLine("Habits Pending Today: ${pendingHabits.size} of $totalHabits")
            pendingHabits.take(4).forEach {
              appendLine(" • \"${it.title}\": ${it.currentProgress}/${it.dailyTarget} ${it.unit} (Streak: ${it.streakDays}d)")
            }
          }
          appendLine()
        }

        CoachIntent.DAY_REVIEW -> {
          appendLine("[FOCUS: DAY REVIEW & RETROSPECTIVE]")
          appendLine("Tasks Accomplished: $completedTasks of $totalTasks (${if (totalTasks > 0) (completedTasks * 100) / totalTasks else 0}%)")
          appendLine("Focus Time Logged: ${completedFocusMin}m (Planned: ${totalPlannedMin}m)")
          if (completedTasksList.isNotEmpty()) {
            appendLine("Completed Tasks:")
            completedTasksList.forEach { task ->
              appendLine(" ✓ \"${task.title}\" (${task.estimatedMinutes}m, ${task.category.displayName})")
            }
          }
          if (remainingTasks.isNotEmpty()) {
            appendLine("Unfinished Tasks:")
            remainingTasks.forEach { task ->
              appendLine(" ✗ \"${task.title}\" (Scheduled: ${task.time}, Category: ${task.category.displayName})")
            }
          }
          appendLine("Habits Logged: $completedHabits of $totalHabits completed today (Streak: ${context.summary.currentStreak} days)")
          appendLine()
        }

        CoachIntent.GOAL_GUIDANCE -> {
          appendLine("[FOCUS: ACTIVE GOALS & MOMENTUM]")
          if (activeGoalsList.isNotEmpty()) {
            appendLine("Active Goals (${activeGoalsList.size}):")
            activeGoalsList.forEach { goal ->
              appendLine(" • Goal: \"${goal.title}\"")
              appendLine("   Progress: ${goal.progressPercentage}% (${goal.currentProgress}/${goal.targetProgress} ${goal.unit})")
              appendLine("   Deadline: ${goal.deadline} | Category: ${goal.category.displayName} | Type: ${goal.goalType}")
              if (goal.description.isNotBlank()) {
                appendLine("   Notes: ${goal.description.take(120)}")
              }
            }
          } else {
            appendLine("No active goals found in DayFlow database.")
          }
          if (completedGoalsList.isNotEmpty()) {
            appendLine("Recently Completed Goals: ${completedGoalsList.size}")
          }
          appendLine()
        }

        CoachIntent.HABIT_STREAK -> {
          appendLine("[FOCUS: HABITS & CONSISTENCY]")
          appendLine("Current Daily Streak: ${context.summary.currentStreak} days")
          if (context.statisticsData != null) {
            appendLine("Best Recorded Streak: ${context.statisticsData.bestStreak} days")
          }
          appendLine("Habits Today ($completedHabits of $totalHabits finished):")
          context.todayHabits.forEach { habit ->
            val status = if (habit.completedToday) "COMPLETED" else "${habit.currentProgress}/${habit.dailyTarget} ${habit.unit}"
            appendLine(" • \"${habit.title}\": $status | Streak: ${habit.streakDays}d | Target: ${habit.targetPerWeek}/week")
          }
          appendLine()
        }

        CoachIntent.STATISTICS_PRODUCTIVITY -> {
          appendLine("[FOCUS: STATISTICS & PRODUCTIVITY METRICS]")
          appendLine("Today Task Completion: $completedTasks/$totalTasks (${if (totalTasks > 0) (completedTasks * 100) / totalTasks else 0}%)")
          appendLine("Today Focus Time: ${context.summary.focusMinutes} minutes")
          if (context.statisticsData != null) {
            val stats = context.statisticsData
            appendLine("Window Range: ${stats.timeRange.label}")
            appendLine("Overall Tasks Completed in Window: ${stats.tasksCompleted} of ${stats.tasksPlanned} (${stats.completionRate}%)")
            appendLine("Total Focus Time in Window: ${stats.totalFocusMinutes}m (Average: ${stats.avgFocusMinutes}m/session)")
            appendLine("Current Streak: ${stats.currentStreak}d | Best Streak: ${stats.bestStreak}d")
            if (stats.categoryStats.isNotEmpty()) {
              appendLine("Category Breakdown:")
              stats.categoryStats.take(5).forEach { cat ->
                appendLine(" • ${cat.category.displayName}: ${cat.completedCount}/${cat.totalCount} tasks (${cat.percentage}%)")
              }
            }
          }
          appendLine()
        }

        CoachIntent.GENERAL -> {
          // Compact full snapshot for general guidance
          appendLine("[SUMMARY OVERVIEW]")
          appendLine("Tasks: $completedTasks/$totalTasks done (${completedFocusMin}m focus logged)")
          appendLine("Habits: $completedHabits/$totalHabits completed today (Current Streak: ${context.summary.currentStreak}d)")
          if (activeGoalsList.isNotEmpty()) {
            val topGoal = activeGoalsList.first()
            appendLine("Active Goal: \"${topGoal.title}\" (${topGoal.progressPercentage}%, Deadline: ${topGoal.deadline})")
          }
          if (remainingTasks.isNotEmpty()) {
            appendLine("Next Pending Task: \"${remainingTasks.first().title}\" at ${remainingTasks.first().time}")
          }
          appendLine()
        }
      }

      // 2. LIVE DATA SANITIZED SUMMARY
      appendLine("--- COMPLETE LIVE INVENTORY ---")
      appendLine("Tasks: $completedTasks completed, ${remainingTasks.size} pending (Total $totalTasks)")
      if (context.todayTasks.isNotEmpty()) {
        appendLine("All Tasks: " + context.todayTasks.joinToString(", ") { "${it.title} (${if (it.isCompleted) "Done" else "Pending"})" })
      }
      appendLine("Habits: $completedHabits completed, ${totalHabits - completedHabits} pending (Total $totalHabits)")
      if (context.todayHabits.isNotEmpty()) {
        appendLine("All Habits: " + context.todayHabits.joinToString(", ") { "${it.title} (${if (it.completedToday) "Done" else "Pending"}, Streak: ${it.streakDays}d)" })
      }
      appendLine("Goals: ${activeGoalsList.size} active, ${completedGoalsList.size} completed")
      if (context.activeGoals.isNotEmpty()) {
        appendLine("All Goals: " + context.activeGoals.joinToString(", ") { "${it.title} (${it.progressPercentage}%)" })
      }
      appendLine("Streak: ${context.summary.currentStreak} days | Logged Focus: ${context.summary.focusMinutes}m")
      appendLine("===============================")
      
      if (context.memories.isNotEmpty()) {
        appendLine()
        appendLine("--- AI MEMORY (USER PREFERENCES & CONTEXT) ---")
        context.memories.forEach { mem ->
          appendLine(" • [${mem.category}]: ${mem.text}")
        }
        appendLine("==============================================")
      }
    }
  }
}

enum class AiResponseMode {
  LIVE_GEMINI,
  LOCAL_FALLBACK
}

data class CoachStreamResult(
  val text: String,
  val mode: AiResponseMode,
  val isErrorFallback: Boolean = false,
  val errorMessage: String? = null
)

object DayFlowAiService {

  private const val MODEL_NAME = "gemini-3.5-flash"
  private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  /**
   * Resolves the active API key. Prefers user-saved key in Preferences,
   * falling back to BuildConfig key if present and not a placeholder.
   */
  fun resolveApiKey(userKey: String): String {
    val trimmedUserKey = userKey.trim()
    if (trimmedUserKey.isNotBlank()) return trimmedUserKey

    val buildConfigKey = try {
      BuildConfig.GEMINI_API_KEY.trim()
    } catch (_: Throwable) {
      ""
    }
    if (buildConfigKey.isNotBlank() &&
      buildConfigKey != "MY_GEMINI_API_KEY" &&
      buildConfigKey != "PLACEHOLDER_API_KEY"
    ) {
      return buildConfigKey
    }
    return ""
  }

  fun hasApiKey(userKey: String): Boolean {
    return resolveApiKey(userKey).isNotBlank()
  }

  /**
   * Tests connection to Gemini API by sending a minimal token ping request to gemini-3.5-flash.
   */
  suspend fun testConnection(apiKey: String): ConnectionTestResult = withContext(Dispatchers.IO) {
    val key = resolveApiKey(apiKey)
    if (key.isBlank()) {
      return@withContext ConnectionTestResult.InvalidKey("No API key provided. Please enter a valid Gemini API key.")
    }

    val requestUrl = "$BASE_URL/$MODEL_NAME:generateContent?key=$key"
    val jsonBody = JSONObject().apply {
      val contentsArray = JSONArray()
      val contentObj = JSONObject()
      val partsArray = JSONArray()
      val partObj = JSONObject()
      partObj.put("text", "ping")
      partsArray.put(partObj)
      contentObj.put("parts", partsArray)
      contentsArray.put(contentObj)
      put("contents", contentsArray)

      val genConfig = JSONObject()
      genConfig.put("maxOutputTokens", 5)
      put("generationConfig", genConfig)
    }

    val request = Request.Builder()
      .url(requestUrl)
      .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
      .build()

    try {
      val response = okHttpClient.newCall(request).execute()
      val code = response.code
      val body = response.body?.string().orEmpty()

      when (code) {
        200 -> ConnectionTestResult.Success
        400 -> {
          val errorMsg = parseErrorMessage(body) ?: "Bad request: Gemini parameters were invalid."
          ConnectionTestResult.InvalidKey(errorMsg)
        }
        401 -> {
          ConnectionTestResult.InvalidKey("Invalid API key. Please verify your Gemini API key.")
        }
        403 -> {
          ConnectionTestResult.InvalidKey("Gemini API access denied. Ensure your API key has Generative Language API access enabled.")
        }
        404 -> {
          ConnectionTestResult.Error("Gemini model or endpoint not found ($MODEL_NAME). Please verify endpoint configuration.")
        }
        429 -> ConnectionTestResult.QuotaExhausted("Gemini API quota or rate limit has been exceeded.")
        in 500..599 -> ConnectionTestResult.Error("Gemini service is temporarily unavailable (HTTP $code).")
        else -> ConnectionTestResult.Error("Unexpected response from Gemini API (HTTP $code).")
      }
    } catch (e: UnknownHostException) {
      ConnectionTestResult.NoInternet("Unable to reach Google servers. Please check your internet connection.")
    } catch (e: ConnectException) {
      ConnectionTestResult.NoInternet("Could not establish connection to Gemini servers.")
    } catch (e: SocketTimeoutException) {
      ConnectionTestResult.Timeout("Connection timed out. Please check your connection and try again.")
    } catch (e: IOException) {
      ConnectionTestResult.NoInternet("Network error: ${e.message ?: "Connection failed"}")
    } catch (e: Exception) {
      ConnectionTestResult.Error("Connection test failed: ${e.localizedMessage ?: "Unknown error"}")
    }
  }

  /**
   * Streams Gemini responses progressively via SSE endpoint with rich DayFlow live context.
   */
  suspend fun streamCoachResponse(
    conversationHistory: List<AiChatMessage>,
    context: AiCoachContext,
    language: AiLanguage,
    userApiKey: String,
    onChunk: (String) -> Unit
  ): CoachStreamResult = withContext(Dispatchers.IO) {
    val apiKey = resolveApiKey(userApiKey)
    val latestUserMessage = conversationHistory.lastOrNull { it.isUser }?.text.orEmpty()
    val structuredContext = DayFlowContextBuilder.buildStructuredContext(context, latestUserMessage)

    if (apiKey.isBlank()) {
      val fallback = generateLocalOfflineCoaching(
        userQuery = latestUserMessage,
        context = context,
        language = language
      )
      fallback.chunked(14).forEach { chunk ->
        onChunk(chunk)
      }
      return@withContext CoachStreamResult(
        text = fallback,
        mode = AiResponseMode.LOCAL_FALLBACK
      )
    }

    val requestUrl = "$BASE_URL/$MODEL_NAME:streamGenerateContent?alt=sse&key=$apiKey"
    val systemPrompt = buildSystemPrompt(structuredContext, language)
    val requestJson = buildGeminiMultiTurnRequestBody(systemPrompt, conversationHistory)

    val request = Request.Builder()
      .url(requestUrl)
      .addHeader("Accept", "text/event-stream")
      .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
      .build()

    val accumulatedText = StringBuilder()

    try {
      val response = okHttpClient.newCall(request).execute()
      if (!response.isSuccessful) {
        val code = response.code
        val errorBody = response.body?.string().orEmpty()
        val errorDetail = parseErrorMessage(errorBody) ?: "HTTP $code"
        val fallback = generateLocalOfflineCoaching(
          userQuery = latestUserMessage,
          context = context,
          language = language
        )
        val notice = "[Gemini unavailable ($errorDetail). Providing assistance via DayFlow's mindful offline coach]\n\n"
        val fullResponse = notice + fallback
        fullResponse.chunked(14).forEach { chunk -> onChunk(chunk) }
        return@withContext CoachStreamResult(
          text = fullResponse,
          mode = AiResponseMode.LOCAL_FALLBACK,
          isErrorFallback = true,
          errorMessage = "Gemini API error ($code): $errorDetail"
        )
      }

      val responseBody = response.body ?: throw IOException("Empty response body from Gemini")
      responseBody.byteStream().bufferedReader().use { reader ->
        var line: String?
        while (reader.readLine().also { line = it } != null) {
          val currentLine = line ?: continue
          if (currentLine.startsWith("data: ")) {
            val jsonString = currentLine.substring(6).trim()
            if (jsonString.isNotEmpty() && jsonString != "[DONE]") {
              try {
                val json = JSONObject(jsonString)
                val candidates = json.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text")
                if (!text.isNullOrEmpty()) {
                  accumulatedText.append(text)
                  onChunk(text)
                }
              } catch (_: Exception) {
                // Ignore parse errors on individual SSE frames
              }
            }
          }
        }
      }

      val resultText = accumulatedText.toString().trim()
      if (resultText.isNotBlank()) {
        return@withContext CoachStreamResult(
          text = resultText,
          mode = AiResponseMode.LIVE_GEMINI
        )
      } else {
        val fallback = generateLocalOfflineCoaching(
          userQuery = latestUserMessage,
          context = context,
          language = language
        )
        val notice = "[Gemini returned empty response. Providing assistance via DayFlow offline coach]\n\n"
        val fullResponse = notice + fallback
        onChunk(fullResponse)
        return@withContext CoachStreamResult(
          text = fullResponse,
          mode = AiResponseMode.LOCAL_FALLBACK,
          isErrorFallback = true,
          errorMessage = "Empty Gemini response"
        )
      }
    } catch (e: Exception) {
      if (e is kotlinx.coroutines.CancellationException) {
        throw e
      }
      val fallback = generateLocalOfflineCoaching(
        userQuery = latestUserMessage,
        context = context,
        language = language
      )
      val errorMsg = e.localizedMessage ?: "Network error"
      val notice = "[Could not reach Gemini ($errorMsg). Providing assistance via DayFlow offline coach]\n\n"
      val fullResponse = notice + fallback
      fullResponse.chunked(14).forEach { chunk -> onChunk(chunk) }
      return@withContext CoachStreamResult(
        text = fullResponse,
        mode = AiResponseMode.LOCAL_FALLBACK,
        isErrorFallback = true,
        errorMessage = errorMsg
      )
    }
  }

  private fun buildSystemPrompt(structuredContext: String, language: AiLanguage): String {
    return """
      You are DayFlow's mindful AI productivity coach and intelligent personal advisor.
      
      CORE ROLE & PHILOSOPHY:
      - Personality: Calm, intelligent, grounded, and practical.
      - Never be overly motivational, hyperactive, or cheesy. Strictly avoid hollow hype like "You got this!", "Crush it!", or "Stay awesome!".
      - Provide thoughtful, realistic recommendations that respect the user's energy, daily schedule, and mental bandwidth.
      - Be concise for quick questions, and structured/actionable for planning, daily briefings, and evening reviews.
      
      DAYFLOW LIVE DATA INTELLIGENCE:
      - You have direct access to the user's LIVE, real-time DayFlow data provided below.
      - Always ground your answers in their exact numbers (task counts, pending tasks, habit progress, streak days, goal percentages, focus minutes).
      - NEVER fabricate, guess, or hallucinate metrics, dates, or progress values.
      - If the user asks about an item or metric not present in the DayFlow data, state clearly: "I don't have enough DayFlow data to determine that."
      - You are advisory only. You do NOT automatically create, edit, or delete tasks/habits/goals. You recommend actions clearly.
      
      LANGUAGE & COMMUNICATION:
      - Selected Language Preference: ${language.displayName}
      - Instruction: ${language.instruction}
      - In AUTO mode:
        * If user writes in English -> Respond in clean, elegant English.
        * If user writes in Hindi -> Respond in natural, conversational Hindi.
        * If user writes in conversational Hinglish (Romanized Hindi) -> Respond in natural, warm Hinglish without awkward formal dictionary translation.
      
      $structuredContext
    """.trimIndent()
  }

  private fun buildGeminiMultiTurnRequestBody(
    systemPrompt: String,
    conversationHistory: List<AiChatMessage>
  ): JSONObject {
    val root = JSONObject()

    // 1. System Instruction
    val sysInstruction = JSONObject()
    val sysParts = JSONArray()
    val sysPart = JSONObject()
    sysPart.put("text", systemPrompt)
    sysParts.put(sysPart)
    sysInstruction.put("parts", sysParts)
    root.put("systemInstruction", sysInstruction)

    // 2. Contents (Multi-turn turns)
    val contentsArray = JSONArray()
    // Include last 10 messages for focused context
    val relevantTurns = conversationHistory.takeLast(10)

    for (msg in relevantTurns) {
      if (msg.text.isBlank()) continue
      val turnObj = JSONObject()
      turnObj.put("role", if (msg.isUser) "user" else "model")
      val partsArr = JSONArray()
      val partObj = JSONObject()
      partObj.put("text", msg.text)
      partsArr.put(partObj)
      turnObj.put("parts", partsArr)
      contentsArray.put(turnObj)
    }

    root.put("contents", contentsArray)

    // 3. Generation Config
    val genConfig = JSONObject()
    genConfig.put("temperature", 0.7)
    genConfig.put("maxOutputTokens", 850)
    root.put("generationConfig", genConfig)

    return root
  }

  private fun parseErrorMessage(responseBody: String): String? {
    return try {
      val json = JSONObject(responseBody)
      val error = json.optJSONObject("error")
      error?.optString("message")
    } catch (_: Exception) {
      null
    }
  }

  /**
   * Mindful, accurate, and context-aware local offline coaching for DayFlow.
   * Accurately parses live tasks, habits, active goals, and streak metrics.
   */
  fun generateLocalOfflineCoaching(
    userQuery: String,
    context: AiCoachContext,
    language: AiLanguage
  ): String {
    val totalTasks = context.todayTasks.size
    val completedTasks = context.todayTasks.count { it.isCompleted }
    val remainingTasks = context.todayTasks.filter { !it.isCompleted }
    val streak = context.summary.currentStreak
    val focusMin = context.summary.focusMinutes
    val activeGoal = context.activeGoals.firstOrNull { !it.isCompleted }
    val matchedGoal = if (userQuery.isNotBlank()) {
      context.activeGoals.find { goal ->
        userQuery.contains(goal.title, ignoreCase = true) ||
          goal.title.split(" ").filter { it.length > 3 }.any { word -> userQuery.contains(word, ignoreCase = true) }
      } ?: activeGoal
    } else {
      activeGoal
    }
    val completedHabits = context.todayHabits.count { it.completedToday }
    val totalHabits = context.todayHabits.size

    val isHindi = language == AiLanguage.HINDI ||
      userQuery.contains("aaj", ignoreCase = true) ||
      userQuery.contains("kya", ignoreCase = true) ||
      userQuery.contains("kare", ignoreCase = true) ||
      userQuery.contains("mera", ignoreCase = true) ||
      userQuery.contains("kaise", ignoreCase = true) ||
      userQuery.contains("kaisi", ignoreCase = true) ||
      userQuery.contains("batao", ignoreCase = true) ||
      userQuery.contains("namaste", ignoreCase = true)

    val queryTrimmed = userQuery.trim().lowercase()
    val isGreeting = queryTrimmed.matches(Regex("^(hi|hello|hey|namaste|good morning|good afternoon|good evening|pranam|hola|yo)[!., ]?.*")) ||
      queryTrimmed in listOf("hi", "hello", "hey", "namaste", "hola", "yo")

    val intent = DayFlowContextBuilder.detectIntent(userQuery, context.queryIntent.takeIf { it != CoachIntent.GENERAL }?.let {
      when (it) {
        CoachIntent.DAILY_BRIEFING -> CoachActionType.DAILY_BRIEFING
        CoachIntent.DAY_REVIEW -> CoachActionType.DAY_REVIEW
        CoachIntent.GOAL_GUIDANCE -> CoachActionType.GOAL_GUIDANCE
        else -> null
      }
    })

    if (isHindi) {
      if (isGreeting) {
        return "Namaste! Main DayFlow ka mindful assistant hoon. Aapke schedule, habits aur goals ke sath aapki madad karne ke liye taiyar hoon. Aaj aap kis cheez par dhyan dena chahte hain?"
      }

      return when (intent) {
        CoachIntent.DAILY_BRIEFING -> {
          if (remainingTasks.isNotEmpty()) {
            val topTask = remainingTasks.first()
            val highPriority = remainingTasks.firstOrNull { it.priority == TaskPriority.HIGH }
            val priorityNote = if (highPriority != null) " Sabse pehle high-priority task '${highPriority.title}' (${highPriority.time}) par concentrate karein." else " Shuruat '${topTask.title}' (${topTask.time}) se kijiye."
            "Aaj aapke paas total $totalTasks tasks planned hain, jinme se ${remainingTasks.size} pending hain.$priorityNote Aaj ${completedHabits}/$totalHabits habits track huye hain aur streak $streak days ka hai."
          } else if (totalTasks > 0) {
            "Shaandar! Aaj ke sabhi $totalTasks tasks complete ho chuke hain. Habits poore karein aur shanti se relax karein."
          } else {
            "Aaj aapka schedule khali hai. 1-2 important tasks add karein ya deep focus session plan karein."
          }
        }

        CoachIntent.DAY_REVIEW -> {
          val percent = if (totalTasks > 0) (completedTasks * 100) / totalTasks else 0
          if (completedTasks == totalTasks && totalTasks > 0) {
            "Aaj ka din bahut hi focused raha: sabhi $totalTasks tasks complete ($percent%) aur ${focusMin}m focus log hua. Streak $streak days par maintain hai."
          } else if (completedTasks > 0) {
            val remainingNames = remainingTasks.take(2).joinToString(", ") { "'${it.title}'" }
            "Aaj aapne $completedTasks/$totalTasks tasks poore kiye (${focusMin}m focus). Jo ${remainingTasks.size} tasks baaki hain ($remainingNames), unhe kal ke liye prioritize karein."
          } else {
            "Aaj koi task complete nahi hua. Chote step se shuru karein aur ek chota habit complete karke din ka rhythm banayein."
          }
        }

        CoachIntent.GOAL_GUIDANCE -> {
          if (matchedGoal != null) {
            "Aapka goal '${matchedGoal.title}' filhal ${matchedGoal.progressPercentage}% par hai (${matchedGoal.currentProgress}/${matchedGoal.targetProgress} ${matchedGoal.unit}). Deadline: ${matchedGoal.deadline}. Daily 25-30 minutes focus block lagane se steady progress milegi."
          } else {
            "Filhal koi active goal set nahi hai. Goals screen par jakar apna agla focus target add kar sakte hain."
          }
        }

        CoachIntent.HABIT_STREAK -> {
          "Aapka current streak $streak din ka hai. Aaj $completedHabits/$totalHabits habits complete hain. Consistency small daily habits se banti hai."
        }

        CoachIntent.STATISTICS_PRODUCTIVITY -> {
          "Productivity Snapshot: $completedTasks/$totalTasks tasks completed, ${focusMin}m focus logged, aur $streak-day habit streak."
        }

        else -> {
          if (remainingTasks.isNotEmpty()) {
            "Aapke ${remainingTasks.size} tasks pending hain. Agla kadam: '${remainingTasks.first().title}' par dhyan lagayein."
          } else {
            "Aapka DayFlow data: $completedTasks tasks done, ${focusMin}m focus, aur $streak days streak. Shanti aur clarity ke sath aage badhiye."
          }
        }
      }
    }

    if (isGreeting) {
      return "Hello! I'm your mindful DayFlow coach. I'm here to help you reflect on your day, organize your schedule, and stay steady with your habits and goals. How can I assist you right now?"
    }

    // English responses
    return when (intent) {
      CoachIntent.DAILY_BRIEFING -> {
        if (remainingTasks.isNotEmpty()) {
          val topTask = remainingTasks.first()
          val highPriority = remainingTasks.firstOrNull { it.priority == TaskPriority.HIGH }
          val priorityAdvice = if (highPriority != null) "Anchor your morning around high-priority item '${highPriority.title}' at ${highPriority.time}." else "Begin with '${topTask.title}' at ${topTask.time} to establish steady momentum."
          val goalMention = if (matchedGoal != null) " Keep '${matchedGoal.title}' (${matchedGoal.progressPercentage}%) in mind for your afternoon focus." else ""
          "You have $totalTasks tasks planned today with ${remainingTasks.size} remaining. $priorityAdvice$goalMention You have $completedHabits/$totalHabits habits completed with a $streak-day streak."
        } else if (totalTasks > 0) {
          "All $totalTasks planned tasks for today are already complete. A wonderful moment to reflect on active goals or enjoy a restful evening."
        } else {
          "Your schedule is open today. Choose 1–2 meaningful priorities or set aside time for intentional learning and restorative habits."
        }
      }

      CoachIntent.DAY_REVIEW -> {
        val percent = if (totalTasks > 0) (completedTasks * 100) / totalTasks else 0
        if (completedTasks == totalTasks && totalTasks > 0) {
          "A productive and balanced day. You accomplished all $totalTasks planned tasks ($percent%) and logged ${focusMin}m of focused work, maintaining your $streak-day rhythm."
        } else if (completedTasks > 0) {
          val pendingDetails = remainingTasks.take(2).joinToString(", ") { "'${it.title}'" }
          "You completed $completedTasks of $totalTasks tasks today with ${focusMin}m of focus logged. For the ${remainingTasks.size} remaining item(s) ($pendingDetails), gently roll them into tomorrow without self-judgment."
        } else {
          "No tasks completed yet today. Protect your $streak-day streak by completing one small mindful habit before the day ends."
        }
      }

      CoachIntent.GOAL_GUIDANCE -> {
        if (matchedGoal != null) {
          "For '${matchedGoal.title}', you are currently at ${matchedGoal.progressPercentage}% (${matchedGoal.currentProgress}/${matchedGoal.targetProgress} ${matchedGoal.unit}) with deadline ${matchedGoal.deadline}. Recommended next step: Schedule dedicated 30-minute focus blocks specifically tagged to ${matchedGoal.category.displayName} to keep momentum high."
        } else {
          "You do not have any active goals right now. Consider adding a focused milestone in the Goals section to anchor your weekly priorities."
        }
      }

      CoachIntent.HABIT_STREAK -> {
        "You currently hold a $streak-day consistency streak across your habits. Today, $completedHabits of $totalHabits habits are completed. Consistent repetition is the foundation of sustainable progress."
      }

      CoachIntent.STATISTICS_PRODUCTIVITY -> {
        val percent = if (totalTasks > 0) (completedTasks * 100) / totalTasks else 0
        "Your DayFlow Metrics: Task Completion Rate is $percent% ($completedTasks/$totalTasks), Logged Focus Time is ${focusMin}m, and Habit Consistency Streak is $streak days."
      }

      else -> {
        if (remainingTasks.isNotEmpty()) {
          "Looking at your schedule: $completedTasks/$totalTasks tasks completed, ${focusMin}m focus logged, and a $streak-day streak. Your next scheduled priority is '${remainingTasks.first().title}'."
        } else {
          "Your day is in calm rhythm with all tasks completed, ${focusMin}m of focus, and a $streak-day streak. Continue with steady clarity."
        }
      }
    }
  }

  fun generateDynamicInsights(context: AiCoachContext): List<CoachInsight> {
    val list = mutableListOf<CoachInsight>()

    val completedTasks = context.todayTasks.count { it.isCompleted }
    val totalTasks = context.todayTasks.size
    val streak = context.summary.currentStreak

    if (streak > 0) {
      list.add(
        CoachInsight(
          id = "ci_streak",
          title = "Consistency Momentum",
          description = "You've maintained your rhythm for $streak consecutive days. Keep this positive habit loop going with steady pacing.",
          type = InsightType.HABIT_ALERT,
          timestamp = "Today"
        )
      )
    }

    if (totalTasks > 0) {
      val ratio = (completedTasks * 100) / totalTasks
      list.add(
        CoachInsight(
          id = "ci_focus",
          title = if (ratio >= 50) "High Focus State" else "Daily Focus Window",
          description = if (ratio >= 50) {
            "You've completed $completedTasks of $totalTasks tasks today ($ratio%). Your momentum is strong."
          } else {
            "You have ${totalTasks - completedTasks} tasks awaiting action. Dedicate a 30-minute block for your top priority."
          },
          type = InsightType.PRODUCTIVITY_TIP,
          timestamp = "Today"
        )
      )
    }

    val activeGoal = context.activeGoals.firstOrNull { !it.isCompleted }
    if (activeGoal != null) {
      list.add(
        CoachInsight(
          id = "ci_goal",
          title = "Goal Progress: ${activeGoal.title}",
          description = "Currently at ${activeGoal.currentProgress}/${activeGoal.targetProgress} ${activeGoal.unit} (${activeGoal.progressPercentage}%). Small daily increments compound steadily.",
          type = InsightType.MOTIVATION,
          timestamp = "Today"
        )
      )
    }

    return list
  }
}
