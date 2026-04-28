package com.example.habitboard.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitboard.R
import com.example.habitboard.data.model.Habit
import com.example.habitboard.data.model.HabitRecord
import com.example.habitboard.data.preferences.WeekStart
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToEditDay: (LocalDate) -> Unit,
    viewModel: CalendarViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val today = remember { LocalDate.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.calendar_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.cd_settings))
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
                    .padding(horizontal = 12.dp),
        ) {
            MonthNavigator(
                yearMonth = uiState.yearMonth,
                canGoPrevious = !uiState.yearMonth.minusMonths(1).isBefore(viewModel.earliestMonth),
                canGoNext = uiState.yearMonth.isBefore(YearMonth.now()),
                onPrevious = viewModel::navigateToPreviousMonth,
                onNext = viewModel::navigateToNextMonth,
            )
            Spacer(modifier = Modifier.height(8.dp))
            CalendarGrid(
                yearMonth = uiState.yearMonth,
                habits = uiState.habits,
                recordsByDate = uiState.recordsByDate,
                weekStart = uiState.weekStart,
                today = today,
                onDateClick = { date ->
                    viewModel.selectDate(date)
                    showBottomSheet = true
                },
            )
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
        ) {
            val isPastDate = uiState.selectedDate.isBefore(today)
            DayDetailContent(
                date = uiState.selectedDate,
                habits = uiState.habits,
                records = uiState.recordsByDate[uiState.selectedDate] ?: emptyList(),
                onEditClick =
                    if (isPastDate) {
                        {
                            showBottomSheet = false
                            onNavigateToEditDay(uiState.selectedDate)
                        }
                    } else {
                        null
                    },
            )
        }
    }
}

@Composable
private fun MonthNavigator(
    yearMonth: YearMonth,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onPrevious, enabled = canGoPrevious) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = stringResource(R.string.cd_previous_month))
        }
        Text(
            text = stringResource(R.string.calendar_year_month, yearMonth.year, yearMonth.monthValue),
            style = MaterialTheme.typography.titleMedium,
        )
        IconButton(onClick = onNext, enabled = canGoNext) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = stringResource(R.string.cd_next_month))
        }
    }
}

@Composable
private fun CalendarGrid(
    yearMonth: YearMonth,
    habits: List<Habit>,
    recordsByDate: Map<LocalDate, List<HabitRecord>>,
    weekStart: WeekStart,
    today: LocalDate,
    onDateClick: (LocalDate) -> Unit,
) {
    val dayHeaders =
        if (weekStart == WeekStart.SUNDAY) {
            stringArrayResource(R.array.calendar_day_headers_sun_first).toList()
        } else {
            stringArrayResource(R.array.calendar_day_headers_mon_first).toList()
        }
    val firstDayOffset = firstDayColumnIndex(yearMonth, weekStart)
    val daysInMonth = yearMonth.lengthOfMonth()
    val totalCells = firstDayOffset + daysInMonth
    val rows = (totalCells + 6) / 7

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            dayHeaders.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val dayNumber = row * 7 + col - firstDayOffset + 1
                    if (dayNumber < 1 || dayNumber > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val date = yearMonth.atDay(dayNumber)
                        DayCell(
                            date = date,
                            habits = habits,
                            records = recordsByDate[date] ?: emptyList(),
                            isToday = date == today,
                            isFuture = date.isAfter(today),
                            onClick = { onDateClick(date) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

private fun firstDayColumnIndex(
    yearMonth: YearMonth,
    weekStart: WeekStart,
): Int {
    val value = yearMonth.atDay(1).dayOfWeek.value
    return if (weekStart == WeekStart.SUNDAY) value % 7 else (value - 1) % 7
}

private enum class CellState { FULL, PARTIAL, NONE }

@Composable
private fun DayCell(
    date: LocalDate,
    habits: List<Habit>,
    records: List<HabitRecord>,
    isToday: Boolean,
    isFuture: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val doneCount = records.count { it.isDone }
    val totalCount = habits.size
    val cellState =
        when {
            isFuture || totalCount == 0 -> CellState.NONE
            doneCount == totalCount -> CellState.FULL
            doneCount > 0 -> CellState.PARTIAL
            else -> CellState.NONE
        }

    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val onSurface = MaterialTheme.colorScheme.onSurface

    Box(
        modifier =
            modifier
                .aspectRatio(1f)
                .padding(2.dp)
                .clip(CircleShape)
                .then(
                    when (cellState) {
                        CellState.FULL -> Modifier.background(primary)
                        CellState.PARTIAL -> Modifier.border(1.5.dp, primary, CircleShape)
                        CellState.NONE -> Modifier
                    },
                ).clickable(enabled = !isFuture, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodySmall,
            textDecoration = if (isToday) TextDecoration.Underline else TextDecoration.None,
            color =
                when {
                    cellState == CellState.FULL -> onPrimary
                    isFuture -> onSurface.copy(alpha = 0.3f)
                    else -> onSurface
                },
        )
    }
}

@Composable
private fun DayDetailContent(
    date: LocalDate,
    habits: List<Habit>,
    records: List<HabitRecord>,
    onEditClick: (() -> Unit)? = null,
) {
    val doneCount = records.count { it.isDone }
    val dateStr = date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd (E)", Locale.JAPANESE))

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = dateStr,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            if (onEditClick != null) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.cd_edit_day_records),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (habits.isEmpty()) {
            Text(
                text = stringResource(R.string.calendar_no_habits),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Text(
                text = stringResource(R.string.achievement_count, doneCount, habits.size),
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { doneCount.toFloat() / habits.size },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))
            habits.forEach { habit ->
                val record = records.find { it.habitId == habit.id }
                val isDone = record?.isDone ?: false
                val timeStr =
                    record
                        ?.completedAt
                        ?.format(DateTimeFormatter.ofPattern("HH:mm"))
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (isDone) stringResource(R.string.mark_done) else stringResource(R.string.mark_undone),
                        style = MaterialTheme.typography.bodyMedium,
                        color =
                            if (isDone) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        modifier = Modifier.width(24.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = habit.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color =
                                if (isDone) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                        )
                        if (timeStr != null) {
                            Text(
                                text = stringResource(R.string.completed_at_time, timeStr),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        val memo = record?.memo
                        if (memo != null) {
                            Text(
                                text = memo,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }
    }
}
