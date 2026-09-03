package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
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
import com.example.data.local.dao.ai.AiChatDao
import com.example.data.local.dao.ai.AiMemoryDao
import com.example.data.local.entity.ai.AiChatMessageEntity
import com.example.data.local.entity.ai.AiChatSessionEntity
import com.example.data.local.entity.ai.AiMemoryEntity
import com.example.model.ItemCategory
import com.example.model.TaskPriority
import com.example.model.TaskStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    TaskEntity::class,
    HabitEntity::class,
    HabitCompletionEntity::class,
    GoalEntity::class,
    GoalProgressEntity::class,
    AiChatSessionEntity::class,
    AiChatMessageEntity::class,
    AiMemoryEntity::class
  ],
  version = 5,
  exportSchema = false
)
@TypeConverters(DayFlowConverters::class)
abstract class DayFlowDatabase : RoomDatabase() {

  abstract fun taskDao(): TaskDao
  abstract fun habitDao(): HabitDao
  abstract fun habitCompletionDao(): HabitCompletionDao
  abstract fun goalDao(): GoalDao
  abstract fun goalProgressDao(): GoalProgressDao
  abstract fun aiChatDao(): AiChatDao
  abstract fun aiMemoryDao(): AiMemoryDao

  companion object {
    @Volatile
    private var INSTANCE: DayFlowDatabase? = null

    val MIGRATION_4_5 = object : Migration(4, 5) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE tasks ADD COLUMN status TEXT NOT NULL DEFAULT 'PENDING'")
        db.execSQL("UPDATE tasks SET status = 'COMPLETED' WHERE isCompleted = 1")
        db.execSQL("ALTER TABLE tasks ADD COLUMN exceptionReason TEXT DEFAULT NULL")
      }
    }

    fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): DayFlowDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          DayFlowDatabase::class.java,
          "dayflow_database"
        )
          .addMigrations(MIGRATION_4_5)
          .fallbackToDestructiveMigration()
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
      val todayKey = com.example.util.DateUtils.getTodayDateKey()

      // Seed initial sample tasks for today
      val initialTasks = listOf(
        TaskEntity(
          id = "t1",
          title = "Morning Meditation",
          description = "Guided breathing and mindfulness",
          dueDate = todayKey,
          startTime = "07:00 AM",
          endTime = "07:15 AM",
          category = ItemCategory.MINDFULNESS,
          priority = TaskPriority.MEDIUM,
          status = TaskStatus.COMPLETED,
          isCompleted = true,
          estimatedMinutes = 15
        ),
        TaskEntity(
          id = "t2",
          title = "Deep Work Session",
          description = "High-concentration architecture planning and implementation",
          dueDate = todayKey,
          startTime = "09:00 AM",
          endTime = "11:00 AM",
          category = ItemCategory.WORK,
          priority = TaskPriority.HIGH,
          status = TaskStatus.PENDING,
          isCompleted = false,
          estimatedMinutes = 120
        ),
        TaskEntity(
          id = "t3",
          title = "Review Weekly Goals",
          description = "Evaluate quarterly key results and progress notes",
          dueDate = todayKey,
          startTime = "02:00 PM",
          endTime = "02:30 PM",
          category = ItemCategory.PERSONAL,
          priority = TaskPriority.LOW,
          status = TaskStatus.PENDING,
          isCompleted = false,
          estimatedMinutes = 30
        )
      )
      taskDao.insertTasks(initialTasks)

      // Seed initial sample habits with measurable targets
      val initialHabits = listOf(
        HabitEntity(
          id = "h1",
          name = "Hydration",
          category = ItemCategory.HEALTH,
          scheduleFrequency = "DAILY",
          targetPerWeek = 7,
          dailyTarget = 5,
          unit = "L",
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
          dailyTarget = 30,
          unit = "min",
          streakDays = 8,
          reminderTime = "09:00 PM",
          isActive = true
        )
      )
      habitDao.insertHabits(initialHabits)

      // Record initial progress for today's habits
      habitCompletionDao.insertCompletion(
        HabitCompletionEntity(
          id = "hc_init_1",
          habitId = "h1",
          completionDate = todayKey,
          progressValue = 3,
          isCompleted = false
        )
      )
      habitCompletionDao.insertCompletion(
        HabitCompletionEntity(
          id = "hc_init_2",
          habitId = "h2",
          completionDate = todayKey,
          progressValue = 30,
          isCompleted = true
        )
      )

      // Seed initial sample goals
      val initialGoals = listOf(
        GoalEntity(
          id = "g1",
          title = "Learn Spanish Fluently",
          description = "Practice daily vocabulary, grammar and speaking exercises",
          goalType = "LONG TERM",
          category = ItemCategory.LEARNING,
          currentProgress = 35,
          targetProgress = 100,
          unit = "%",
          deadline = "180d left",
          isCompleted = false
        ),
        GoalEntity(
          id = "g2",
          title = "Launch Portfolio Website",
          description = "Design and build personal developer showcase site",
          goalType = "SHORT TERM",
          category = ItemCategory.WORK,
          currentProgress = 80,
          targetProgress = 100,
          unit = "%",
          deadline = "14d left",
          isCompleted = false
        ),
        GoalEntity(
          id = "g3",
          title = "Read 12 Books",
          description = "Non-fiction, biographies, and technical literature",
          goalType = "SHORT TERM",
          category = ItemCategory.LEARNING,
          currentProgress = 100,
          targetProgress = 100,
          unit = "%",
          deadline = "Dec 2023",
          isCompleted = true
        )
      )
      goalDao.insertGoals(initialGoals)
    }
  }
}
