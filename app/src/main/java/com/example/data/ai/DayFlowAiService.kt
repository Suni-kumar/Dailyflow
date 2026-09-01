package com.example.data.ai

import com.example.BuildConfig
import com.example.model.CoachInsight
import com.example.model.DailyProgressSummary
import com.example.model.GoalItem
import com.example.model.HabitItem
import com.example.model.InsightType
import com.example.model.TaskItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class CoachActionType {
  DAILY_BRIEFING,
  DAY_REVIEW,
  GOAL_GUIDANCE,
  ASK_AI
}

data class AiCoachContext(
  val todayTasks: List<TaskItem>,
  val todayHabits: List<HabitItem>,
  val activeGoals: List<GoalItem>,
  val summary: DailyProgressSummary,
  val selectedDate: String
)

object DayFlowAiService {

  private const val MODEL_NAME = "gemini-3.5-flash"
  private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

  fun isGeminiConfigured(): Boolean {
    val key = try {
      BuildConfig.GEMINI_API_KEY
    } catch (_: Throwable) {
      ""
    }
    return key.isNotBlank() && key != "MY_GEMINI_API_KEY" && key != "PLACEHOLDER_API_KEY"
  }

  suspend fun generateCoachResponse(
    actionType: CoachActionType,
    userQuery: String = "",
    context: AiCoachContext
  ): String = withContext(Dispatchers.IO) {
    val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (_: Throwable) { "" }

    val summarizedContext = buildSummarizedContext(context)

    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "PLACEHOLDER_API_KEY") {
      return@withContext generateLocalOfflineCoaching(actionType, userQuery, context)
    }

    try {
      val promptText = buildPrompt(actionType, userQuery, summarizedContext)
      val jsonBody = buildGeminiRequestBody(promptText)

      val requestUrl = "$BASE_URL/$MODEL_NAME:generateContent?key=$apiKey"
      val request = Request.Builder()
        .url(requestUrl)
        .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
        .build()

      val response = okHttpClient.newCall(request).execute()
      val responseBodyString = response.body?.string().orEmpty()

      if (!response.isSuccessful) {
        // Fallback gracefully to mindful local guidance if API call fails
        return@withContext generateLocalOfflineCoaching(actionType, userQuery, context)
      }

      val responseJson = JSONObject(responseBodyString)
      val candidates = responseJson.optJSONArray("candidates")
      val firstCandidate = candidates?.optJSONObject(0)
      val content = firstCandidate?.optJSONObject("content")
      val parts = content?.optJSONArray("parts")
      val text = parts?.optJSONObject(0)?.optString("text")

      if (!text.isNullOrBlank()) {
        text.trim()
      } else {
        generateLocalOfflineCoaching(actionType, userQuery, context)
      }
    } catch (_: Exception) {
      generateLocalOfflineCoaching(actionType, userQuery, context)
    }
  }

  private fun buildSummarizedContext(context: AiCoachContext): String {
    val totalTasks = context.todayTasks.size
    val completedTasks = context.todayTasks.count { it.isCompleted }
    val incompleteTasks = context.todayTasks.filter { !it.isCompleted }.map { it.title }
    val totalEstimatedMin = context.todayTasks.sumOf { it.estimatedMinutes }

    val totalHabits = context.todayHabits.size
    val completedHabits = context.todayHabits.count { it.completedToday }
    val habitTitles = context.todayHabits.map { "${it.title} (${it.currentProgress}/${it.dailyTarget} ${it.unit})" }

    val goalSummaries = context.activeGoals.filter { !it.isCompleted }.take(3).map {
      "${it.title} (${it.currentProgress}/${it.targetProgress} ${it.unit}, deadline: ${it.deadline})"
    }

    return """
      [Date: ${context.selectedDate}]
      Tasks: $completedTasks/$totalTasks completed (Total planned focus: ${totalEstimatedMin}m).
      Pending task titles: ${incompleteTasks.joinToString(", ").ifEmpty { "All tasks completed!" }}
      Habits: $completedHabits/$totalHabits completed today. Habits: ${habitTitles.joinToString(", ").ifEmpty { "None" }}
      Active Goals: ${goalSummaries.joinToString("; ").ifEmpty { "No active goals set" }}
      Current Streak: ${context.summary.currentStreak} days | Focus Today: ${context.summary.focusMinutes}m
    """.trimIndent()
  }

  private fun buildPrompt(actionType: CoachActionType, userQuery: String, summaryContext: String): String {
    val systemTone = "You are the DayFlow mindful productivity coach. Be calm, encouraging, and concise (under 120 words). Provide actionable, mindful guidance."
    val specificRequest = when (actionType) {
      CoachActionType.DAILY_BRIEFING -> "Give me a calm and intentional morning briefing for today based on my planned tasks and habits."
      CoachActionType.DAY_REVIEW -> "Review my achievements and progress for today, highlighting what went well and offering mindful encouragement."
      CoachActionType.GOAL_GUIDANCE -> "Analyze my active goals and give me 1-2 focused recommendations to maintain momentum."
      CoachActionType.ASK_AI -> "User question: $userQuery"
    }

    return "$systemTone\n\nUser Context:\n$summaryContext\n\nRequest: $specificRequest"
  }

  private fun buildGeminiRequestBody(promptText: String): JSONObject {
    val root = JSONObject()
    val contentsArray = JSONArray()
    val contentObj = JSONObject()
    val partsArray = JSONArray()
    val partObj = JSONObject()
    partObj.put("text", promptText)
    partsArray.put(partObj)
    contentObj.put("parts", partsArray)
    contentsArray.put(contentObj)
    root.put("contents", contentsArray)

    val genConfig = JSONObject()
    genConfig.put("temperature", 0.7)
    genConfig.put("maxOutputTokens", 250)
    root.put("generationConfig", genConfig)

    return root
  }

  private fun generateLocalOfflineCoaching(
    actionType: CoachActionType,
    userQuery: String,
    context: AiCoachContext
  ): String {
    val totalTasks = context.todayTasks.size
    val completedTasks = context.todayTasks.count { it.isCompleted }
    val remainingTasks = context.todayTasks.filter { !it.isCompleted }
    val streak = context.summary.currentStreak
    val focusMin = context.summary.focusMinutes

    return when (actionType) {
      CoachActionType.DAILY_BRIEFING -> {
        if (totalTasks == 0) {
          "Good morning. Your schedule is open today. Take this opportunity to set 1-3 intentional priorities or dedicate time for mindfulness and deep learning."
        } else {
          val nextTask = remainingTasks.firstOrNull()?.title ?: "your priority task"
          "You have $totalTasks tasks planned today with ${remainingTasks.size} remaining. Start your day centering around '$nextTask', pacing yourself with steady intervals."
        }
      }
      CoachActionType.DAY_REVIEW -> {
        if (completedTasks == totalTasks && totalTasks > 0) {
          "Remarkable dedication today! You've accomplished all $totalTasks planned tasks and logged focus hours. Take time tonight to wind down peacefully."
        } else if (completedTasks > 0) {
          "You completed $completedTasks tasks and logged deep focus time. Don't worry about the remaining items—celebrate the progress made today and rest well."
        } else {
          "Today is a gentle step forward. Focus on completing just one small habit before the evening ends to keep your $streak-day streak alive."
        }
      }
      CoachActionType.GOAL_GUIDANCE -> {
        val activeGoal = context.activeGoals.firstOrNull { !it.isCompleted }
        if (activeGoal != null) {
          val percent = if (activeGoal.targetProgress > 0) (activeGoal.currentProgress * 100) / activeGoal.targetProgress else 0
          "For your goal '${activeGoal.title}', you're at $percent% progress. Breaking this down into a 20-minute daily habit will guarantee completion ahead of ${activeGoal.deadline}."
        } else {
          "You don't have any active goals right now. Consider setting a quarterly objective in the Goals tab to guide your daily tasks."
        }
      }
      CoachActionType.ASK_AI -> {
        val queryLower = userQuery.lowercase()
        when {
          queryLower.contains("streak") || queryLower.contains("habit") -> {
            "You are currently holding a $streak-day streak. Consistency thrives on small, frictionless daily actions rather than high-intensity bursts."
          }
          queryLower.contains("focus") || queryLower.contains("time") -> {
            "You've logged ${focusMin}m of focus time. For optimal cognitive retention, pair 45-minute focus sessions with 5-minute breathing pauses."
          }
          queryLower.contains("task") || queryLower.contains("todo") -> {
            if (remainingTasks.isNotEmpty()) {
              "You have ${remainingTasks.size} tasks pending. Try tackling '${remainingTasks.first().title}' first to create momentum."
            } else {
              "All planned tasks are completed for today. Great job staying focused."
            }
          }
          else -> {
            "I'm observing your rhythm: $completedTasks tasks completed, $streak-day streak, and steady habit consistency. Prioritize your single most meaningful intention next."
          }
        }
      }
    }
  }

  fun generateDynamicInsights(context: AiCoachContext): List<CoachInsight> {
    val list = mutableListOf<CoachInsight>()

    val completedTasks = context.todayTasks.count { it.isCompleted }
    val totalTasks = context.todayTasks.size
    val streak = context.summary.currentStreak

    // Insight 1: Consistency
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

    // Insight 2: Task Execution
    if (totalTasks > 0) {
      val ratio = (completedTasks * 100) / totalTasks
      list.add(
        CoachInsight(
          id = "ci_focus",
          title = if (ratio >= 50) "High Focus State" else "Daily Focus Window",
          description = if (ratio >= 50) {
            "You've completed $completedTasks of $totalTasks tasks today ($ratio%). Your afternoon momentum is strong."
          } else {
            "You have ${totalTasks - completedTasks} tasks awaiting action. Dedicate a 30-minute block for your top priority."
          },
          type = InsightType.PRODUCTIVITY_TIP,
          timestamp = "Today"
        )
      )
    }

    // Insight 3: Goal Alignment
    val activeGoal = context.activeGoals.firstOrNull { !it.isCompleted }
    if (activeGoal != null) {
      list.add(
        CoachInsight(
          id = "ci_goal",
          title = "Goal Progress: ${activeGoal.title}",
          description = "Currently at ${activeGoal.currentProgress}/${activeGoal.targetProgress} ${activeGoal.unit}. Small daily increments will compound steadily.",
          type = InsightType.MOTIVATION,
          timestamp = "Today"
        )
      )
    }

    return list
  }
}
