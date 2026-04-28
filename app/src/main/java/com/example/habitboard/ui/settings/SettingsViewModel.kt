package com.example.habitboard.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.habitboard.data.preferences.UserPreferences
import com.example.habitboard.data.preferences.WeekStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val userPrefs = UserPreferences(application)

    private val _weekStart = MutableStateFlow(userPrefs.weekStart)
    val weekStart: StateFlow<WeekStart> = _weekStart.asStateFlow()

    fun setWeekStart(weekStart: WeekStart) {
        userPrefs.weekStart = weekStart
        _weekStart.value = weekStart
    }
}
