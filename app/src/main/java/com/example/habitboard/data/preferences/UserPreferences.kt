package com.example.habitboard.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

enum class WeekStart { SUNDAY, MONDAY }

class UserPreferences(
    context: Context,
) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var weekStart: WeekStart
        get() = WeekStart.valueOf(prefs.getString(KEY_WEEK_START, WeekStart.SUNDAY.name)!!)
        set(value) {
            prefs.edit().putString(KEY_WEEK_START, value.name).apply()
        }

    fun weekStartFlow(): Flow<WeekStart> =
        callbackFlow {
            val listener =
                SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                    if (key == KEY_WEEK_START) trySend(weekStart)
                }
            prefs.registerOnSharedPreferenceChangeListener(listener)
            trySend(weekStart)
            awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
        }

    companion object {
        private const val KEY_WEEK_START = "week_start"
    }
}
