package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

  @Query("SELECT * FROM goals ORDER BY createdAt DESC")
  fun getAllGoals(): Flow<List<GoalEntity>>

  @Query("SELECT * FROM goals WHERE isCompleted = 0 ORDER BY createdAt DESC")
  fun getActiveGoals(): Flow<List<GoalEntity>>

  @Query("SELECT * FROM goals WHERE isCompleted = 1 ORDER BY createdAt DESC")
  fun getCompletedGoals(): Flow<List<GoalEntity>>

  @Query("SELECT * FROM goals WHERE id = :id LIMIT 1")
  suspend fun getGoalById(id: String): GoalEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGoal(goal: GoalEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGoals(goals: List<GoalEntity>)

  @Update
  suspend fun updateGoal(goal: GoalEntity)

  @Query("UPDATE goals SET currentProgress = :progress, isCompleted = :isCompleted WHERE id = :id")
  suspend fun updateGoalProgress(id: String, progress: Int, isCompleted: Boolean)

  @Delete
  suspend fun deleteGoal(goal: GoalEntity)

  @Query("DELETE FROM goals WHERE id = :id")
  suspend fun deleteGoalById(id: String)
}
