package com.example.data.local

import androidx.room.withTransaction
import com.example.data.local.entity.GoalEntity
import com.example.data.local.entity.GoalProgressEntity
import com.example.data.local.entity.HabitCompletionEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.TaskEntity
import com.example.model.ItemCategory
import com.example.model.TaskPriority
import com.example.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class ImportResultSummary(
  val tasksCount: Int,
  val habitsCount: Int,
  val completionsCount: Int,
  val goalsCount: Int,
  val progressCount: Int
) {
  val message: String
    get() = "Restored $tasksCount tasks, $habitsCount habits, $goalsCount goals."
}

object DayFlowBackupManager {

  const val CURRENT_BACKUP_VERSION = 1
  const val APP_IDENTIFIER = "DayFlow"

  suspend fun exportBackupJson(
    database: DayFlowDatabase?,
    preferencesManager: UserPreferencesManager?
  ): String = withContext(Dispatchers.IO) {
    val root = JSONObject()
    root.put("backupVersion", CURRENT_BACKUP_VERSION)
    root.put("app", APP_IDENTIFIER)
    root.put("exportTimestamp", System.currentTimeMillis())
    root.put("exportDate", DateUtils.getTodayDateKey())

    val tasksArray = JSONArray()
    val habitsArray = JSONArray()
    val completionsArray = JSONArray()
    val goalsArray = JSONArray()
    val progressArray = JSONArray()

    if (database != null) {
      val taskEntities = database.taskDao().getAllTasksList()
      for (task in taskEntities) {
        val tObj = JSONObject()
        tObj.put("id", task.id)
        tObj.put("title", task.title)
        tObj.put("description", task.description)
        tObj.put("dueDate", task.dueDate)
        tObj.put("startTime", task.startTime)
        tObj.put("endTime", task.endTime)
        tObj.put("category", task.category.name)
        tObj.put("priority", task.priority.name)
        tObj.put("isCompleted", task.isCompleted)
        tObj.put("estimatedMinutes", task.estimatedMinutes)
        tObj.put("createdAt", task.createdAt)
        tasksArray.put(tObj)
      }

      val habitEntities = database.habitDao().getAllHabitsList()
      for (habit in habitEntities) {
        val hObj = JSONObject()
        hObj.put("id", habit.id)
        hObj.put("name", habit.name)
        hObj.put("category", habit.category.name)
        hObj.put("scheduleFrequency", habit.scheduleFrequency)
        hObj.put("targetPerWeek", habit.targetPerWeek)
        hObj.put("dailyTarget", habit.dailyTarget)
        hObj.put("unit", habit.unit)
        hObj.put("streakDays", habit.streakDays)
        hObj.put("reminderTime", habit.reminderTime)
        hObj.put("isActive", habit.isActive)
        hObj.put("createdAt", habit.createdAt)
        habitsArray.put(hObj)
      }

      val completions = database.habitCompletionDao().getAllCompletionsList()
      for (comp in completions) {
        val cObj = JSONObject()
        cObj.put("id", comp.id)
        cObj.put("habitId", comp.habitId)
        cObj.put("completionDate", comp.completionDate)
        cObj.put("progressValue", comp.progressValue)
        cObj.put("isCompleted", comp.isCompleted)
        cObj.put("completedAt", comp.completedAt)
        completionsArray.put(cObj)
      }

      val goalEntities = database.goalDao().getAllGoalsList()
      for (goal in goalEntities) {
        val gObj = JSONObject()
        gObj.put("id", goal.id)
        gObj.put("title", goal.title)
        gObj.put("description", goal.description)
        gObj.put("goalType", goal.goalType)
        gObj.put("category", goal.category.name)
        gObj.put("currentProgress", goal.currentProgress)
        gObj.put("targetProgress", goal.targetProgress)
        gObj.put("unit", goal.unit)
        gObj.put("deadline", goal.deadline)
        gObj.put("isCompleted", goal.isCompleted)
        gObj.put("createdAt", goal.createdAt)
        goalsArray.put(gObj)
      }

      val progressEntities = database.goalProgressDao().getAllProgressRecordsList()
      for (prog in progressEntities) {
        val pObj = JSONObject()
        pObj.put("id", prog.id)
        pObj.put("goalId", prog.goalId)
        pObj.put("progressValue", prog.progressValue)
        pObj.put("delta", prog.delta)
        pObj.put("note", prog.note.orEmpty())
        pObj.put("recordedAt", prog.recordedAt)
        progressArray.put(pObj)
      }
    }

    root.put("tasks", tasksArray)
    root.put("habits", habitsArray)
    root.put("habitCompletions", completionsArray)
    root.put("goals", goalsArray)
    root.put("goalProgress", progressArray)

    if (preferencesManager != null) {
      root.put("preferences", preferencesManager.exportToJson())
    }

    return@withContext root.toString(2)
  }

  suspend fun importBackupJson(
    jsonString: String,
    database: DayFlowDatabase?,
    preferencesManager: UserPreferencesManager?
  ): Result<ImportResultSummary> = withContext(Dispatchers.IO) {
    try {
      if (jsonString.isBlank()) {
        return@withContext Result.failure(IllegalArgumentException("Backup file is empty."))
      }

      val root = try {
        JSONObject(jsonString)
      } catch (e: Exception) {
        return@withContext Result.failure(IllegalArgumentException("Invalid JSON format. Please select a valid DayFlow backup file."))
      }

      // Check app identifier
      val app = root.optString("app", "")
      if (!app.equals(APP_IDENTIFIER, ignoreCase = true)) {
        return@withContext Result.failure(IllegalArgumentException("This backup file is not from DayFlow."))
      }

      // Check backup version
      val version = root.optInt("backupVersion", -1)
      if (version < 1) {
        return@withContext Result.failure(IllegalArgumentException("Unsupported backup format."))
      }
      if (version > CURRENT_BACKUP_VERSION) {
        return@withContext Result.failure(IllegalArgumentException("Backup is from a newer version of DayFlow. Please update your app."))
      }

      // Parse tasks
      val tasksToInsert = mutableListOf<TaskEntity>()
      val tasksArray = root.optJSONArray("tasks")
      if (tasksArray != null) {
        for (i in 0 until tasksArray.length()) {
          val t = tasksArray.optJSONObject(i) ?: continue
          val categoryName = t.optString("category", ItemCategory.WORK.name)
          val priorityName = t.optString("priority", TaskPriority.MEDIUM.name)
          tasksToInsert.add(
            TaskEntity(
              id = t.optString("id", UUID.randomUUID().toString()),
              title = t.optString("title", "Untitled Task"),
              description = t.optString("description", ""),
              dueDate = t.optString("dueDate", DateUtils.getTodayDateKey()),
              startTime = t.optString("startTime", "09:00 AM"),
              endTime = t.optString("endTime", "10:00 AM"),
              category = try { ItemCategory.valueOf(categoryName) } catch (_: Exception) { ItemCategory.WORK },
              priority = try { TaskPriority.valueOf(priorityName) } catch (_: Exception) { TaskPriority.MEDIUM },
              isCompleted = t.optBoolean("isCompleted", false),
              estimatedMinutes = t.optInt("estimatedMinutes", 30),
              createdAt = t.optLong("createdAt", System.currentTimeMillis())
            )
          )
        }
      }

      // Parse habits
      val habitsToInsert = mutableListOf<HabitEntity>()
      val habitsArray = root.optJSONArray("habits")
      if (habitsArray != null) {
        for (i in 0 until habitsArray.length()) {
          val h = habitsArray.optJSONObject(i) ?: continue
          val categoryName = h.optString("category", ItemCategory.HEALTH.name)
          habitsToInsert.add(
            HabitEntity(
              id = h.optString("id", UUID.randomUUID().toString()),
              name = h.optString("name", "New Habit"),
              category = try { ItemCategory.valueOf(categoryName) } catch (_: Exception) { ItemCategory.HEALTH },
              scheduleFrequency = h.optString("scheduleFrequency", "DAILY"),
              targetPerWeek = h.optInt("targetPerWeek", 7),
              dailyTarget = h.optInt("dailyTarget", 1),
              unit = h.optString("unit", "times"),
              streakDays = h.optInt("streakDays", 0),
              reminderTime = h.optString("reminderTime", "08:00 AM"),
              isActive = h.optBoolean("isActive", true),
              createdAt = h.optLong("createdAt", System.currentTimeMillis())
            )
          )
        }
      }

      // Parse completions
      val completionsToInsert = mutableListOf<HabitCompletionEntity>()
      val completionsArray = root.optJSONArray("habitCompletions")
      if (completionsArray != null) {
        for (i in 0 until completionsArray.length()) {
          val c = completionsArray.optJSONObject(i) ?: continue
          completionsToInsert.add(
            HabitCompletionEntity(
              id = c.optString("id", UUID.randomUUID().toString()),
              habitId = c.optString("habitId", ""),
              completionDate = c.optString("completionDate", DateUtils.getTodayDateKey()),
              progressValue = c.optInt("progressValue", 0),
              isCompleted = c.optBoolean("isCompleted", false),
              completedAt = c.optLong("completedAt", System.currentTimeMillis())
            )
          )
        }
      }

      // Parse goals
      val goalsToInsert = mutableListOf<GoalEntity>()
      val goalsArray = root.optJSONArray("goals")
      if (goalsArray != null) {
        for (i in 0 until goalsArray.length()) {
          val g = goalsArray.optJSONObject(i) ?: continue
          val categoryName = g.optString("category", ItemCategory.PERSONAL.name)
          goalsToInsert.add(
            GoalEntity(
              id = g.optString("id", UUID.randomUUID().toString()),
              title = g.optString("title", "New Goal"),
              description = g.optString("description", ""),
              goalType = g.optString("goalType", "SHORT TERM"),
              category = try { ItemCategory.valueOf(categoryName) } catch (_: Exception) { ItemCategory.PERSONAL },
              currentProgress = g.optInt("currentProgress", 0),
              targetProgress = g.optInt("targetProgress", 100),
              unit = g.optString("unit", "%"),
              deadline = g.optString("deadline", "In 30 days"),
              isCompleted = g.optBoolean("isCompleted", false),
              createdAt = g.optLong("createdAt", System.currentTimeMillis())
            )
          )
        }
      }

      // Parse goal progress
      val progressToInsert = mutableListOf<GoalProgressEntity>()
      val progressArray = root.optJSONArray("goalProgress")
      if (progressArray != null) {
        for (i in 0 until progressArray.length()) {
          val p = progressArray.optJSONObject(i) ?: continue
          val noteVal = p.optString("note", p.optString("notes", ""))
          progressToInsert.add(
            GoalProgressEntity(
              id = p.optString("id", UUID.randomUUID().toString()),
              goalId = p.optString("goalId", ""),
              progressValue = p.optInt("progressValue", 0),
              delta = p.optInt("delta", 0),
              recordedAt = p.optLong("recordedAt", System.currentTimeMillis()),
              note = noteVal.ifEmpty { null }
            )
          )
        }
      }

      // Apply to database atomically
      if (database != null) {
        database.withTransaction {
          database.taskDao().clearAllTasks()
          database.habitDao().clearAllHabits()
          database.habitCompletionDao().clearAllCompletions()
          database.goalDao().clearAllGoals()
          database.goalProgressDao().clearAllProgressRecords()

          if (tasksToInsert.isNotEmpty()) {
            database.taskDao().insertTasks(tasksToInsert)
          }
          if (habitsToInsert.isNotEmpty()) {
            database.habitDao().insertHabits(habitsToInsert)
          }
          if (completionsToInsert.isNotEmpty()) {
            database.habitCompletionDao().insertCompletions(completionsToInsert)
          }
          if (goalsToInsert.isNotEmpty()) {
            database.goalDao().insertGoals(goalsToInsert)
          }
          if (progressToInsert.isNotEmpty()) {
            database.goalProgressDao().insertProgressRecords(progressToInsert)
          }
        }
      }

      // Restore safe preferences
      val prefsObj = root.optJSONObject("preferences")
      if (prefsObj != null && preferencesManager != null) {
        preferencesManager.importFromJson(prefsObj)
      }

      val summary = ImportResultSummary(
        tasksCount = tasksToInsert.size,
        habitsCount = habitsToInsert.size,
        completionsCount = completionsToInsert.size,
        goalsCount = goalsToInsert.size,
        progressCount = progressToInsert.size
      )

      Result.success(summary)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
