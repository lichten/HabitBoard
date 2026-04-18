package com.example.habitboard.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitboard.data.model.Habit
import com.example.habitboard.data.model.HabitRecord
import com.example.habitboard.data.preferences.UserPreferences
import com.example.habitboard.data.preferences.WeekStart
import com.example.habitboard.data.repository.HabitRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val yearMonth: YearMonth = YearMonth.now(),
    val habits: List<Habit> = emptyList(),
    val recordsByDate: Map<LocalDate, List<HabitRecord>> = emptyMap(),
    val selectedDate: LocalDate = LocalDate.now(),
    val weekStart: WeekStart = WeekStart.SUNDAY
)

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HabitRepository(application)
    private val userPrefs = UserPreferences(application)

    private val _yearMonth = MutableStateFlow(YearMonth.now())
    private val _selectedDate = MutableStateFlow(LocalDate.now())

    val earliestMonth: YearMonth = YearMonth.now().minusMonths(11)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CalendarUiState> = combine(
        _yearMonth,
        userPrefs.weekStartFlow()
    ) { yearMonth, weekStart -> yearMonth to weekStart }
        .flatMapLatest { (yearMonth, weekStart) ->
            val startDate = yearMonth.atDay(1)
            val endDate = yearMonth.atEndOfMonth()
            combine(
                repository.getHabits(),
                repository.getRecordsForDateRange(startDate, endDate),
                _selectedDate
            ) { habits, records, selectedDate ->
                CalendarUiState(
                    yearMonth = yearMonth,
                    habits = habits,
                    recordsByDate = records.groupBy { it.date },
                    selectedDate = selectedDate,
                    weekStart = weekStart
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CalendarUiState()
        )

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun navigateToPreviousMonth() {
        val prev = _yearMonth.value.minusMonths(1)
        if (!prev.isBefore(earliestMonth)) _yearMonth.value = prev
    }

    fun navigateToNextMonth() {
        val next = _yearMonth.value.plusMonths(1)
        if (!next.isAfter(YearMonth.now())) _yearMonth.value = next
    }
}
