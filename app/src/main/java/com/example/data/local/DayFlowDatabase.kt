package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.converter.DayFlowConverters
import com.example.data.local.dao.GoalDao
import com.example.data.local.dao.GoalProgressDao
import com.example.data.local.dao.HabitCompletionDao
import com.example.data.local.dao.HabitDao
import com.example.data.local.dao.TaskDao
import com.example.data.local.entity.GoalEntity
import com.example.data.local.entity.GoalProgressEntity
import com.example.data.local.entity.HabitCompletionEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.TaskEntity
import com.example.model.ItemCategory
import com.example.model.TaskPriority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    TaskEntity::class,
    HabitEntity::class,
    HabitCompletionEntity::class,
    GoalEntity::class,
    GoalProgressEntity::class
  ],
  version = 1,
  exportSchema = false
)
@TypeConverters(DayFlowConverters::class)
abstract class DayFlowDatabase : RoomDatabase() {

  abstract fun taskDao(): TaskDao
  abstract fun habitDao(): HabitDao
  abstract fun habitCompletionDao(): HabitCompletionDao
  abstract fun goalDao(): GoalDao
  abstract fun goalProgressDao(): GoalProgressDao

  companion object {
    @Volatile
    private var INSTANCE: DayFlowDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): DayFlowDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          DayFlowDatabase::class.java,
          "dayflow_database"
        )
          .addCallback(DayFlowDatabaseCallback(scope))
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DayFlowDatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            prepopulateDatabase(database)
          }
        }
      }
    }

    suspend fun prepopulateDatabase(db: DayFlowDatabase) {
      val taskDao = db.taskDao()
      val habitDao = db.habitDao()
      val habitCompletionDao = db.habitCompletionDao()
      val goalDao = db.goalDao()

      // Seed initial sample tasks
      val initialTasks = listOf(
        TaskEntity(
          id = "t1",
          title = "Morning Meditation",
          description = "Guided breathing and mindfulness",
          dueDate = "Today",
          startTime = "07:00 AM",
          endTime = "07:15 AM",
          category = ItemCategory.MINDFULNESS,
          priority = TaskPriority.MEDIUM,
          isCompleted = true,
          estimatedMinutes = 15
        ),
        TaskEntity(
          id = "t2",
          title = "Deep Work Session",
          description = "High-concentration architecture planning and implementation",
          dueDate = "Today",
          startTime = "09:00 AM",
          endTime = "11:00 AM",
          category = ItemCategory.WORK,
          priority = TaskPriority.HIGH,
          isCompleted = false,
          estimatedMinutes = 120
        ),
        TaskEntity(
          id = "t3",
          title = "Review Weekly Goals",
          description = "Evaluate quarterly key results and progress notes",
          dueDate = "Today",
          startTime = "02:00 PM",
          endTime = "02:30 PM",
          category = ItemCategory.PERSONAL,
          priority = TaskPriority.LOW,
          isCompleted = false,
          estimatedMinutes = 30
        )
      )
      taskDao.insertTasks(initialTasks)

      // Seed initial sample habits
      val initialHabits = listOf(
        HabitEntity(
          id = "h1",
          name = "Hydration",
          category = ItemCategory.HEALTH,
          scheduleFrequency = "DAILY",
          targetPerWeek = 7,
          streakDays = 14,
          reminderTime = "08:00 AM",
          isActive = true
        ),
        HabitEntity(
          id = "h2",
          name = "Reading",
          category = ItemCategory.LEARNING,
          scheduleFrequency = "DAILY",
          targetPerWeek = 7,
          streakDays = 8,
          reminderTime = "09:00 PM",
          isActive = true
        )
      )
      habitDao.insertHabits(initialHabits)

      // Record completion for reading habit today
      habitCompletionDao.insertCompletion(
        HabitCompletionEntity(
          id = "hc_init_1",
          habitId = "h2",
          completionDate = "Today"
        )
      )

      // Seed initial sample goals
      val initialGoals = listOf(
        GoalEntity(
          id = "g1",
          title = "Run 50km this month",
          description = "Maintain cardiovascular health and stamina",
          category = ItemCategory.FITNESS,
          currentProgress = 32,
          targetProgress = 50,
          unit = "km",
          deadline = "In 12 days",
          isCompleted = false
        ),
        GoalEntity(
          id = "g2",
          title = "Complete Kotlin Course",
          description = "Master Compose and modern reactive architecture",
          category = ItemCategory.LEARNING,
          currentProgress = 8,
          targetProgress = 12,
          unit = "modules",
          deadline = "In 18 days",
          isCompleted = false
        )
      )
      goalDao.insertGoals(initialGoals)
    }
  }
}
