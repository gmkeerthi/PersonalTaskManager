package com.example.personaltaskmanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant


@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueEpochMillis: Long? = null,
    val priority: Int = 0, // 0 low, 1 medium, 2 high
    val completed: Boolean = false,
    val reminderEpochMillis: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)