package com.example.habitboard.ui.manage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitboard.R
import com.example.habitboard.data.model.Habit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageScreen(
    onBack: () -> Unit,
    viewModel: ManageViewModel = viewModel(),
) {
    val habits by viewModel.habits.collectAsState()
    var newHabitName by remember { mutableStateOf("") }
    var editingHabit by remember { mutableStateOf<Habit?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.manage_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
        ) {
            Text(stringResource(R.string.manage_add_section), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = newHabitName,
                    onValueChange = { newHabitName = it },
                    label = { Text(stringResource(R.string.habit_name_label)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        viewModel.addHabit(newHabitName)
                        newHabitName = ""
                    },
                    enabled = newHabitName.isNotBlank(),
                ) {
                    Text(stringResource(R.string.action_add))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(stringResource(R.string.manage_list_section), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(habits, key = { it.id }) { habit ->
                    HabitManageRow(
                        habit = habit,
                        onEdit = { editingHabit = habit },
                        onDelete = { viewModel.deleteHabit(habit) },
                    )
                }
            }
        }
    }

    editingHabit?.let { habit ->
        EditHabitDialog(
            habit = habit,
            onConfirm = { newName ->
                viewModel.updateHabit(habit, newName)
                editingHabit = null
            },
            onDismiss = { editingHabit = null },
        )
    }
}

@Composable
private fun HabitManageRow(
    habit: Habit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = habit.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.cd_edit))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.cd_delete))
            }
        }
    }
}

@Composable
private fun EditHabitDialog(
    habit: Habit,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember(habit.id) { mutableStateOf(habit.name) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_habit_dialog_title)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.habit_name_label)) },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank(),
            ) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
    )
}
