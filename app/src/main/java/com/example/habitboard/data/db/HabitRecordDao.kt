package com.example.habitboard.data.db

import androidx.room.*
import com.example.habitboard.data.model.HabitRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface HabitRecordDao {
    @Query("SELECT * FROM habit_records WHERE date = :date")
    fun getRecordsForDate(date: LocalDate): Flow<List<HabitRecord>>

    @Query("SELECT * FROM habit_records WHERE date = :date")
    suspend fun getRecordsForDateSync(date: LocalDate): List<HabitRecord>

    @Query("SELECT * FROM habit_records WHERE date >= :startDate AND date <= :endDate")
    fun getRecordsForDateRange(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Flow<List<HabitRecord>>

    @Upsert
    suspend fun upsert(record: HabitRecord)
}
