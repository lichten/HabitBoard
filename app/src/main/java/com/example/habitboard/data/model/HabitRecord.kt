package com.example.habitboard.data.model

import androidx.room.Entity
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "habit_records", primaryKeys = ["habitId", "date"])
data class HabitRecord(
    val habitId: Int,
    val date: LocalDate,
    val isDone: Boolean,
    val completedAt: LocalDateTime? = null,
    val memo: String? = null
)
