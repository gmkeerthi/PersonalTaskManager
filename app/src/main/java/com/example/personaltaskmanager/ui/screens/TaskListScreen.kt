@file:OptIn(ExperimentalFoundationApi::class)
package com.example.personaltaskmanager.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.personaltaskmanager.data.model.TaskEntity
import com.example.personaltaskmanager.viewmodel.TaskViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement

@Composable
fun TaskListScreen(
    viewModel: TaskViewModel = hiltViewModel(),
    onNavigateToCreate: ()->Unit = {},
    onOpen: (Long)->Unit = {}
){
    val tasks by viewModel.uiState.collectAsState()
    val scaffoldState = rememberScaffoldState()


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colors.onPrimary)
            }
        },
        scaffoldState = scaffoldState
    ) { padding ->
        Column(modifier = Modifier.padding(padding)){
            SearchAndFilterRow(viewModel)


            if (tasks.isEmpty()){
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center){
                    Text("No tasks — add one!", style = MaterialTheme.typography.h6)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()){
// group into Today / Tomorrow / Overdue / Upcoming / Completed
                    val grouped = groupTasks(tasks)
                    grouped.forEach { (header, items) ->
                        stickyHeader {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colors.surface,
                                elevation = 8.dp
                            ) {
                                Text(
                                    text = header,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.subtitle2,
                                    color = MaterialTheme.colors.primary
                                )
                            }
                        }
                        items(items){ task ->
                            TaskRow(task, onOpen = onOpen, onToggle = { viewModel.toggleComplete(task) }, onDelete = { viewModel.delete(task) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchAndFilterRow(vm: TaskViewModel){
    val q by remember { mutableStateOf("") }
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)){
        OutlinedTextField(value = q, onValueChange = { vm.setQuery(it) }, modifier = Modifier.weight(1f), placeholder = { Text("Search") })
        FilterChipRow(vm)
    }
}


@Composable
fun FilterChipRow(vm: TaskViewModel){
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
        IconButton(onClick = { vm.setFilter(TaskViewModel.Filter.ALL)}){ Text("All") }
        IconButton(onClick = { vm.setFilter(TaskViewModel.Filter.TODAY)}){ Text("Today") }
        IconButton(onClick = { vm.setFilter(TaskViewModel.Filter.COMPLETED)}){ Text("Done") }
        IconButton(onClick = { vm.setFilter(TaskViewModel.Filter.OVERDUE)}){ Text("Overdue") }
    }
}

@Composable
fun TaskRow(
    task: TaskEntity,
    onOpen: (Long) -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth()
            .animateContentSize(),
        elevation = 6.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Task title
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.h6,
                    color = if (task.completed) MaterialTheme.colors.primary.copy(alpha = 0.6f) else MaterialTheme.colors.onSurface
                )

                // Description (short preview)
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description.take(60) + if (task.description.length > 60) "..." else "",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Due date
                task.dueEpochMillis?.let { millis ->
                    val sdf = java.text.SimpleDateFormat("MMM dd, yyyy • hh:mm a", java.util.Locale.getDefault())
                    val text = sdf.format(java.util.Date(millis))
                    Text(
                        text = "Due: $text",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.secondary
                    )
                }

                // Priority badge
                PriorityBadge(priority = task.priority)
            }

            // Action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onToggle) {
                    if (task.completed) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = MaterialTheme.colors.primary)
                    } else {
                        Icon(Icons.Default.RadioButtonUnchecked, contentDescription = "Mark as done")
                    }
                }
                IconButton(onClick = { onOpen(task.id) }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Open")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colors.error)
                }
            }
        }
    }
}

@Composable
fun PriorityBadge(priority: Int) {
    val (text, color) = when (priority) {
        2 -> "High" to MaterialTheme.colors.error
        1 -> "Medium" to MaterialTheme.colors.secondary
        else -> "Low" to MaterialTheme.colors.primary
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.caption
        )
    }
}

fun groupTasks(tasks: List<TaskEntity>): List<Pair<String, List<TaskEntity>>> {
    val today = Calendar.getInstance()
    today.set(Calendar.HOUR_OF_DAY, 0)
    today.set(Calendar.MINUTE, 0)
    today.set(Calendar.SECOND, 0)
    today.set(Calendar.MILLISECOND, 0)

    val tomorrow = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }

    val todayList = mutableListOf<TaskEntity>()
    val tomorrowList = mutableListOf<TaskEntity>()
    val overdue = mutableListOf<TaskEntity>()
    val upcoming = mutableListOf<TaskEntity>()
    val completed = mutableListOf<TaskEntity>()

    for (t in tasks) {
        if (t.completed) {
            completed.add(t)
        } else {
            val dueMillis = t.dueEpochMillis
            if (dueMillis == null) {
                upcoming.add(t)
            } else {
                val dueCal = Calendar.getInstance().apply { timeInMillis = dueMillis }
                when {
                    isSameDay(dueCal, today) -> todayList.add(t)
                    isSameDay(dueCal, tomorrow) -> tomorrowList.add(t)
                    dueCal.before(today) -> overdue.add(t)
                    else -> upcoming.add(t)
                }
            }
        }
    }

    val groups = mutableListOf<Pair<String, List<TaskEntity>>>()
    if (todayList.isNotEmpty()) groups.add("Today" to todayList)
    if (tomorrowList.isNotEmpty()) groups.add("Tomorrow" to tomorrowList)
    if (overdue.isNotEmpty()) groups.add("Overdue" to overdue)
    if (upcoming.isNotEmpty()) groups.add("Upcoming" to upcoming)
    if (completed.isNotEmpty()) groups.add("Completed" to completed)
    return groups
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
