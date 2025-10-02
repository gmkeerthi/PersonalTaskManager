package com.example.personaltaskmanager.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

fun scheduleReminder(
    context: Context,
    taskId: Long,
    title: String,
    desc: String,
    reminderTime: Long
) {
    val delay = reminderTime - System.currentTimeMillis()
    if (delay <= 0) return  // Don't schedule if it's in the past

    val input = workDataOf(
        "title" to title,
        "text" to desc
    )

    val request = OneTimeWorkRequestBuilder<ReminderWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(input)
        .addTag("reminder_$taskId") // for cancellation later
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        "reminder_$taskId", // unique name
        ExistingWorkPolicy.REPLACE, // replace if already scheduled
        request
    )
}

fun cancelReminder(context: Context, taskId: Long) {
    WorkManager.getInstance(context).cancelAllWorkByTag("reminder_$taskId")
}
