package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.HabitCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCompletionDao {

  @Query("SELECT * FROM habit_completions WHERE completionDate = :date")
  fun getCompletionsForDate(date: String): Flow<List<HabitCompletionEntity>>

  @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY completedAt DESC")
  fun getCompletionsForHabit(habitId: String): Flow<List<HabitCompletionEntity>>

  @Query("SELECT * FROM habit_completions WHERE habitId = :habitId AND completionDate = :date LIMIT 1")
  suspend fun getCompletion(habitId: String, date: String): HabitCompletionEntity?

  @Query("SELECT COUNT(*) FROM habit_completions WHERE habitId = :habitId AND isCompleted = 1")
  fun getCompletionCountForHabit(habitId: String): Flow<Int>

  @Query("SELECT * FROM habit_completions ORDER BY completedAt DESC")
  fun getAllCompletions(): Flow<List<HabitCompletionEntity>>

  @Query("SELECT * FROM habit_completions ORDER BY completedAt DESC")
  suspend fun getAllCompletionsList(): List<HabitCompletionEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCompletion(completion: HabitCompletionEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCompletions(completions: List<HabitCompletionEntity>)

  @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND completionDate = :date")
  suspend fun deleteCompletion(habitId: String, date: String)

  @Query("DELETE FROM habit_completions WHERE id = :id")
  suspend fun deleteCompletionById(id: String)

  @Query("DELETE FROM habit_completions")
  suspend fun clearAllCompletions()
}
