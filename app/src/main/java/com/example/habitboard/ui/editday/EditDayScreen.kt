package com.example.habitboard.ui.editday

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitboard.R
import com.example.habitboard.data.model.HabitRecord
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDayScreen(
    date: LocalDate,
    onBack: () -> Unit,
    viewModel: EditDayViewModel = viewModel(factory = EditDayViewModel.factory(date)),
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateStr = date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd (E)", Locale.JAPANESE))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(dateStr) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        if (uiState.habits.isEmpty()) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.edit_day_no_habits),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.habits, key = { it.id }) { habit ->
                    val record = uiState.recordsByHabitId[habit.id]
                    val isDone = record?.isDone ?: false
                    EditDayHabitRow(
                        habitName = habit.name,
                        isDone = isDone,
                        record = record,
                        onToggle = { viewModel.toggle(habit.id, isDone) },
                    )
                }
            }
        }
    }
}

@Composable
private fun EditDayHabitRow(
    habitName: String,
    isDone: Boolean,
    record: HabitRecord?,
    onToggle: () -> Unit,
) {
    val timeStr = record?.completedAt?.format(DateTimeFormatter.ofPattern("HH:mm"))

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
                if (record?.memo != null) {
                    Text(
                        text = record.memo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
