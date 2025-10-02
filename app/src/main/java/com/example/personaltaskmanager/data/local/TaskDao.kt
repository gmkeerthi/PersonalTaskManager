package com.example.personaltaskmanager.data.local

import androidx.room.*
import com.example.personaltaskmanager.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY completed ASC, dueEpochMillis IS NULL, dueEpochMillis ASC, priority DESC, createdAt DESC")
    fun getAllTasksFlow(): Flow<List<TaskEntity>>


    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): TaskEntity?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long


    @Update
    suspend fun update(task: TaskEntity)


    @Delete
    suspend fun delete(task: TaskEntity)


    @Query("DELETE FROM tasks WHERE completed = 1")
    suspend fun deleteCompleted()
}