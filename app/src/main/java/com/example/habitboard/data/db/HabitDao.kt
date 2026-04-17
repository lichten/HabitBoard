package com.example.habitboard.data.db

import androidx.room.*
import com.example.habitboard.data.model.Habit
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY `order` ASC, id ASC")
    fun getAll(): Flow<List<Habit>>

    @Query("SELECT * FROM habits ORDER BY `order` ASC, id ASC")
    suspend fun getAllSync(): List<Habit>

    @Insert
    suspend fun insert(habit: Habit)

    @Update
    suspend fun update(habit: Habit)

    @Delete
    suspend fun delete(habit: Habit)
}
