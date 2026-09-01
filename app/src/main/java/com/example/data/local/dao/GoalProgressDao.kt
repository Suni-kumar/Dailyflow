package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.GoalProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalProgressDao {

  @Query("SELECT * FROM goal_progress_records WHERE goalId = :goalId ORDER BY recordedAt DESC")
  fun getProgressRecordsForGoal(goalId: String): Flow<List<GoalProgressEntity>>

  @Query("SELECT * FROM goal_progress_records ORDER BY recordedAt DESC")
  fun getAllProgressRecords(): Flow<List<GoalProgressEntity>>

  @Query("SELECT * FROM goal_progress_records ORDER BY recordedAt DESC")
  suspend fun getAllProgressRecordsList(): List<GoalProgressEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProgressRecord(record: GoalProgressEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProgressRecords(records: List<GoalProgressEntity>)

  @Query("DELETE FROM goal_progress_records WHERE goalId = :goalId")
  suspend fun deleteRecordsForGoal(goalId: String)

  @Query("DELETE FROM goal_progress_records")
  suspend fun clearAllProgressRecords()
}
