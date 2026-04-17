package com.example.habitboard.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitboard.data.model.Habit
import com.example.habitboard.data.repository.HabitRepository
import com.example.habitboard.widget.HabitWidget
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class MainUiState(
    val habits: List<Habit> = emptyList(),
    val records: Map<Int, Boolean> = emptyMap(),
    val today: LocalDate = LocalDate.now()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HabitRepository(application)
    private val today = LocalDate.now()

    val uiState: StateFlow<MainUiState> = combine(
        repository.getHabits(),
        repository.getRecordsForDate(today)
    ) { habits, records ->
        val recordMap = records.associate { it.habitId to it.isDone }
        MainUiState(habits = habits, records = recordMap, today = today)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
    )

    fun toggle(habitId: Int, currentDone: Boolean) {
        viewModelScope.launch {
            repository.toggleRecord(habitId, today, !currentDone)
            HabitWidget().updateAll(getApplication())
        }
    }
}
