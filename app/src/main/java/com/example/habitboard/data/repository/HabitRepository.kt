package com.example.habitboard.data.repository

import android.content.Context
import com.example.habitboard.data.db.AppDatabase
import com.example.habitboard.data.model.Habit
import com.example.habitboard.data.model.HabitRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

class HabitRepository(
    context: Context,
) {
    private val db = AppDatabase.getInstance(context)
    private val habitDao = db.habitDao()
    private val recordDao = db.habitRecordDao()

    fun getHabits(): Flow<List<Habit>> = habitDao.getAll()

    suspend fun getHabitsSync(): List<Habit> = habitDao.getAllSync()

    fun getRecordsForDate(date: LocalDate): Flow<List<HabitRecord>> = recordDao.getRecordsForDate(date)

    fun getRecordsForDateRange(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Flow<List<HabitRecord>> = recordDao.getRecordsForDateRange(startDate, endDate)

    suspend fun getRecordsForDateSync(date: LocalDate): List<HabitRecord> = recordDao.getRecordsForDateSync(date)

    suspend fun insertHabit(habit: Habit) = habitDao.insert(habit)

    suspend fun updateHabit(habit: Habit) = habitDao.update(habit)

    suspend fun deleteHabit(habit: Habit) = habitDao.delete(habit)

    suspend fun toggleRecord(
        habitId: Int,
        date: LocalDate,
        isDone: Boolean,
    ) {
        val existing = recordDao.getRecordsForDateSync(date).find { it.habitId == habitId }
        val completedAt = if (isDone) LocalDateTime.now() else null
        recordDao.upsert(HabitRecord(habitId, date, isDone, completedAt, existing?.memo))
    }

    suspend fun updateMemo(
        habitId: Int,
        date: LocalDate,
        memo: String?,
    ) {
        val existing = recordDao.getRecordsForDateSync(date).find { it.habitId == habitId }
        val record =
            existing?.copy(memo = memo)
                ?: HabitRecord(habitId, date, false, null, memo)
        recordDao.upsert(record)
    }
}
