package com.example.habitboard.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitboard.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToManage: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    viewModel: MainViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateStr = uiState.today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
    val doneCount = uiState.recordsByHabitId.values.count { it.isDone }
    val totalCount = uiState.habits.size
    var memoEditingHabitId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.main_title, dateStr)) },
                actions = {
                    IconButton(onClick = onNavigateToCalendar) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = stringResource(R.string.cd_calendar))
                    }
                    IconButton(onClick = onNavigateToManage) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.cd_manage))
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToManage) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_add_habit))
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (totalCount > 0) {
                Text(
                    text = stringResource(R.string.achievement_count, doneCount, totalCount),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (uiState.habits.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(R.string.main_no_habits),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
                items(uiState.habits, key = { it.id }) { habit ->
                    val record = uiState.recordsByHabitId[habit.id]
                    val isDone = record?.isDone ?: false
                    HabitRow(
                        habitName = habit.name,
                        isDone = isDone,
                        completedAt = record?.completedAt,
                        memo = record?.memo,
                        onToggle = { viewModel.toggle(habit.id, isDone) },
                        onMemoClick = { memoEditingHabitId = habit.id },
                    )
                }
                item { Spacer(modifier = Modifier.height(72.dp)) }
            }
        }
    }

    memoEditingHabitId?.let { habitId ->
        val currentMemo = uiState.recordsByHabitId[habitId]?.memo ?: ""
        MemoEditDialog(
            initialMemo = currentMemo,
            onConfirm = { memo ->
                viewModel.updateMemo(habitId, memo.ifBlank { null })
                memoEditingHabitId = null
            },
            onDismiss = { memoEditingHabitId = null },
        )
    }
}

@Composable
fun HabitRow(
    habitName: String,
    isDone: Boolean,
    completedAt: LocalDateTime?,
    memo: String?,
    onToggle: () -> Unit,
    onMemoClick: () -> Unit,
) {
    val timeStr = completedAt?.format(DateTimeFormatter.ofPattern("HH:mm"))

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = habitName, style = MaterialTheme.typography.bodyLarge)
                if (timeStr != null) {
                    Text(
                        text = stringResource(R.string.completed_at_time, timeStr),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(
                    modifier =
                        Modifier
                            .clickable(onClick = onMemoClick)
                            .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.cd_edit_memo),
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = memo ?: stringResource(R.string.memo_empty_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color =
                            if (memo != null) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = onToggle,
                colors =
                    if (isDone) {
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    } else {
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    },
            ) {
                Text(
                    text =
                        if (isDone) {
                            stringResource(R.string.button_done)
                        } else {
                            stringResource(R.string.button_not_done)
                        },
                    color =
                        if (isDone) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }
        }
    }
}

@Composable
private fun MemoEditDialog(
    initialMemo: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var memo by remember { mutableStateOf(initialMemo) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.memo_dialog_title)) },
        text = {
            OutlinedTextField(
                value = memo,
                onValueChange = { memo = it },
                label = { Text(stringResource(R.string.memo_label)) },
                placeholder = { Text(stringResource(R.string.memo_placeholder)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(memo) }) { Text(stringResource(R.string.action_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
    )
}
