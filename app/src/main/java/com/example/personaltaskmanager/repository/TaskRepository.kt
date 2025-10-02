package com.example.personaltaskmanager.repository

import com.example.personaltaskmanager.data.local.TaskDao
import com.example.personaltaskmanager.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow


class TaskRepository(private val dao: TaskDao) {
    fun tasksFlow(): Flow<List<TaskEntity>> = dao.getAllTasksFlow()
    suspend fun get(id: Long) = dao.getById(id)
    suspend fun insert(task: TaskEntity) = dao.insert(task)
    suspend fun update(task: TaskEntity) = dao.update(task)
    suspend fun delete(task: TaskEntity) = dao.delete(task)
    suspend fun deleteCompleted() = dao.deleteCompleted()
}