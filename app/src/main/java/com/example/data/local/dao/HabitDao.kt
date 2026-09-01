package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

  @Query("SELECT * FROM habits ORDER BY createdAt ASC")
  fun getAllHabits(): Flow<List<HabitEntity>>

  @Query("SELECT * FROM habits WHERE isActive = 1 ORDER BY createdAt ASC")
  fun getActiveHabits(): Flow<List<HabitEntity>>

  @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
  suspend fun getHabitById(id: String): HabitEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHabit(habit: HabitEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHabits(habits: List<HabitEntity>)

  @Update
  suspend fun updateHabit(habit: HabitEntity)

  @Query("UPDATE habits SET streakDays = :streakDays WHERE id = :id")
  suspend fun updateHabitStreak(id: String, streakDays: Int)

  @Delete
  suspend fun deleteHabit(habit: HabitEntity)

  @Query("DELETE FROM habits WHERE id = :id")
  suspend fun deleteHabitById(id: String)
}
