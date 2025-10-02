package com.example.personaltaskmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.personaltaskmanager.viewmodel.TaskViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskDetailScreen(
    taskId: Long,
    onBack: () -> Unit,
    vm: TaskViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsState()
    val task = state.firstOrNull { it.id == taskId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (task == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Task not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title
                Text(task.title, style = MaterialTheme.typography.h5)

                // Description
                if (task.description.isNotBlank()) {
                    Text(task.description, style = MaterialTheme.typography.body1)
                }

                // Due date
                task.dueEpochMillis?.let { millis ->
                    val dateStr = formatDate(millis)
                    Text("Due: $dateStr", style = MaterialTheme.typography.body2)
                }

                // Reminder
                task.reminderEpochMillis?.let { millis ->
                    val reminderStr = formatDate(millis)
                    Text("Reminder: $reminderStr", style = MaterialTheme.typography.body2)
                }

                // Priority
                val priorityText = when (task.priority) {
                    2 -> "High"
                    1 -> "Medium"
                    else -> "Low"
                }
                Text("Priority: $priorityText", style = MaterialTheme.typography.body2)

                Spacer(Modifier.height(16.dp))

                // Actions
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { vm.toggleComplete(task) }) {
                        Text(if (task.completed) "Mark undone" else "Mark done")
                    }
                    Button(onClick = {
                        vm.delete(task)
                        onBack()
                    }) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

// Helper to format millis to readable date/time
fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    return sdf.format(Date(millis))
}
