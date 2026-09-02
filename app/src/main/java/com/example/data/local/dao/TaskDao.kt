package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

  @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
  fun getAllTasks(): Flow<List<TaskEntity>>

  @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
  suspend fun getAllTasksList(): List<TaskEntity>

  @Query("SELECT * FROM tasks WHERE dueDate = :date ORDER BY createdAt DESC")
  fun getTasksByDate(date: String): Flow<List<TaskEntity>>

  @Query("SELECT * FROM tasks WHERE dueDate = :date ORDER BY createdAt DESC")
  suspend fun getTasksByDateSync(date: String): List<TaskEntity>

  @Query("SELECT * FROM tasks WHERE isCompleted = :completed ORDER BY createdAt DESC")
  fun getTasksByCompletionStatus(completed: Boolean): Flow<List<TaskEntity>>

  @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
  suspend fun getTaskById(id: String): TaskEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: TaskEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTasks(tasks: List<TaskEntity>)

  @Update
  suspend fun updateTask(task: TaskEntity)

  @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :id")
  suspend fun updateTaskCompletion(id: String, isCompleted: Boolean)

  @Delete
  suspend fun deleteTask(task: TaskEntity)

  @Query("DELETE FROM tasks WHERE id = :id")
  suspend fun deleteTaskById(id: String)

  @Query("DELETE FROM tasks")
  suspend fun clearAllTasks()
}
