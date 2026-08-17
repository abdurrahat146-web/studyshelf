package com.studyshelf.app.ui.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import com.studyshelf.app.data.model.RoutineEntry
import com.studyshelf.app.notifications.RoutineAlarmScheduler
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Calendar
import java.util.UUID

private val android.content.Context.routineDataStore by preferencesDataStore(name = "routines")
private val ROUTINES_KEY = stringPreferencesKey("routine_list_json")
private val routineJson = Json { ignoreUnknownKeys = true }

private val dayLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
// Calendar.DAY_OF_WEEK: 1=Sunday ... 7=Saturday, matches dayLabels index+1

@Composable
fun RoutineScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var routines by remember { mutableStateOf<List<RoutineEntry>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }

    suspend fun load(): List<RoutineEntry> {
        val prefs = context.routineDataStore.data.first()
        val raw = prefs[ROUTINES_KEY] ?: return emptyList()
        return try {
            routineJson.decodeFromString(raw)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun save(list: List<RoutineEntry>) {
        context.routineDataStore.edit { it[ROUTINES_KEY] = routineJson.encodeToString(list) }
        // Re-arm all alarms to reflect the latest list (cancel+reschedule is
        // simplest & safest given AlarmManager has no bulk-update API).
        RoutineAlarmScheduler.cancelAll(context, routines)
        RoutineAlarmScheduler.scheduleAll(context, list)
    }

    LaunchedEffect(Unit) {
        routines = load()
        RoutineAlarmScheduler.scheduleAll(context, routines)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Study Routine") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add reminder")
            }
        }
    ) { padding ->
        if (routines.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No study reminders yet.\nTap + to add one.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(routines, key = { it.id }) { routine ->
                    Card {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(routine.title, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    String.format("%02d:%02d · %s", routine.hour, routine.minute,
                                        routine.daysOfWeek.joinToString(", ") { dayLabels[it - 1] }),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Switch(
                                checked = routine.enabled,
                                onCheckedChange = { checked ->
                                    scope.launch {
                                        val updated = routines.map {
                                            if (it.id == routine.id) it.copy(enabled = checked) else it
                                        }
                                        routines = updated
                                        save(updated)
                                    }
                                }
                            )
                            IconButton(onClick = {
                                scope.launch {
                                    val updated = routines.filterNot { it.id == routine.id }
                                    routines = updated
                                    save(updated)
                                }
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddRoutineDialog(
            onDismiss = { showAddDialog = false },
            onSave = { entry ->
                scope.launch {
                    val updated = routines + entry
                    routines = updated
                    save(updated)
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
private fun AddRoutineDialog(onDismiss: () -> Unit, onSave: (RoutineEntry) -> Unit) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var hour by remember { mutableStateOf(18) }
    var minute by remember { mutableStateOf(0) }
    val selectedDays = remember { mutableStateListOf<Int>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Study Reminder") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("What are you studying?") },
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))

                OutlinedButton(onClick = {
                    TimePickerDialog(
                        context,
                        { _, h, m -> hour = h; minute = m },
                        hour, minute, true
                    ).show()
                }) {
                    Text(String.format("Time: %02d:%02d", hour, minute))
                }

                Spacer(Modifier.height(12.dp))
                Text("Repeat on:", style = MaterialTheme.typography.labelSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    dayLabels.forEachIndexed { index, label ->
                        val dayOfWeek = index + 1 // Calendar.DAY_OF_WEEK is 1-based, Sunday=1
                        FilterChip(
                            selected = selectedDays.contains(dayOfWeek),
                            onClick = {
                                if (selectedDays.contains(dayOfWeek)) selectedDays.remove(dayOfWeek)
                                else selectedDays.add(dayOfWeek)
                            },
                            label = { Text(label) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank() && selectedDays.isNotEmpty(),
                onClick = {
                    onSave(
                        RoutineEntry(
                            id = UUID.randomUUID().toString(),
                            title = title,
                            hour = hour,
                            minute = minute,
                            daysOfWeek = selectedDays.toList(),
                            enabled = true
                        )
                    )
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
