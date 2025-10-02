package com.example.personaltaskmanager.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.personaltaskmanager.data.model.TaskEntity
import com.example.personaltaskmanager.viewmodel.TaskViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import com.example.personaltaskmanager.worker.scheduleReminder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditTaskScreen(
    onSaved: () -> Unit,
    vm: TaskViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(0) }

    var dueDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var reminderDateTime by remember { mutableStateOf<LocalDateTime?>(null) }

    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Description
        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        // Priority Buttons
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = priority == 0, onClick = { priority = 0 }, label = { Text("Low") })
            FilterChip(selected = priority == 1, onClick = { priority = 1 }, label = { Text("Medium") })
            FilterChip(selected = priority == 2, onClick = { priority = 2 }, label = { Text("High") })
        }

        // Due Date/Time Picker
        Button(
            onClick = {
                val now = LocalDateTime.now()
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                dueDateTime = LocalDateTime.of(year, month + 1, day, hour, minute)
                            },
                            now.hour, now.minute, false
                        ).show()
                    },
                    now.year, now.monthValue - 1, now.dayOfMonth
                ).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                dueDateTime?.format(dateFormatter) ?: "Pick Due Date & Time"
            )
        }

        // Reminder Picker
        Button(
            onClick = {
                val now = LocalDateTime.now()
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                reminderDateTime = LocalDateTime.of(year, month + 1, day, hour, minute)
                            },
                            now.hour, now.minute, false
                        ).show()
                    },
                    now.year, now.monthValue - 1, now.dayOfMonth
                ).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                reminderDateTime?.format(dateFormatter) ?: "Set Reminder (optional)"
            )
        }

        Spacer(Modifier.height(20.dp))

        // Save Button
        val context = LocalContext.current

        Button(onClick = {
            if (title.isNotBlank()) {
                val task = TaskEntity(
                    title = title,
                    description = desc,
                    priority = priority,
                    dueEpochMillis = dueDateTime?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
                    reminderEpochMillis = reminderDateTime?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
                )
                vm.insert(task) { id ->
                    task.reminderEpochMillis?.let { reminderTime ->
                        scheduleReminder(
                            context = context,
                            taskId = id,
                            title = task.title,
                            desc = task.description,
                            reminderTime = reminderTime
                        )
                    }
                    onSaved()
                }
            }
        }) {
            Text("Save Task")
        }

    }
}
