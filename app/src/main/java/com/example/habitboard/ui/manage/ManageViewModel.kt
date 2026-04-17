package com.example.habitboard.ui.manage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitboard.data.model.Habit
import com.example.habitboard.data.repository.HabitRepository
import androidx.glance.appwidget.updateAll
import com.example.habitboard.widget.HabitWidget
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HabitRepository(application)

    val habits: StateFlow<List<Habit>> = repository.getHabits()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addHabit(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val nextOrder = (habits.value.maxOfOrNull { it.order } ?: -1) + 1
            repository.insertHabit(Habit(name = name.trim(), order = nextOrder))
            HabitWidget().updateAll(getApplication())
        }
    }

    fun updateHabit(habit: Habit, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            repository.updateHabit(habit.copy(name = newName.trim()))
            HabitWidget().updateAll(getApplication())
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
            HabitWidget().updateAll(getApplication())
        }
    }
}
