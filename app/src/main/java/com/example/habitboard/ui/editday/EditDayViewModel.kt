package com.example.habitboard.ui.editday

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.habitboard.data.model.Habit
import com.example.habitboard.data.model.HabitRecord
import com.example.habitboard.data.repository.HabitRepository
import com.example.habitboard.widget.HabitWidget
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class EditDayUiState(
    val habits: List<Habit> = emptyList(),
    val recordsByHabitId: Map<Int, HabitRecord> = emptyMap()
)

class EditDayViewModel(application: Application, val date: LocalDate) : AndroidViewModel(application) {
    private val repository = HabitRepository(application)

    val uiState: StateFlow<EditDayUiState> = combine(
        repository.getHabits(),
        repository.getRecordsForDate(date)
    ) { habits, records ->
        EditDayUiState(
            habits = habits,
            recordsByHabitId = records.associateBy { it.habitId }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EditDayUiState()
    )

    fun toggle(habitId: Int, currentDone: Boolean) {
        viewModelScope.launch {
            repository.toggleRecord(habitId, date, !currentDone)
            HabitWidget().updateAll(getApplication())
        }
    }

    companion object {
        fun factory(date: LocalDate) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val app = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return EditDayViewModel(app, date) as T
            }
        }
    }
}
