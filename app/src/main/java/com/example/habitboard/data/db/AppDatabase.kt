package com.example.habitboard.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.habitboard.data.model.Habit
import com.example.habitboard.data.model.HabitRecord

@Database(entities = [Habit::class, HabitRecord::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

    abstract fun habitRecordDao(): HabitRecordDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        private val migration1To2 =
            object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE habit_records ADD COLUMN completedAt TEXT")
                }
            }

        private val migration2To3 =
            object : Migration(2, 3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE habit_records ADD COLUMN memo TEXT")
                }
            }

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                Room
                    .databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "habit_board.db",
                    ).addMigrations(migration1To2, migration2To3)
                    .build()
                    .also { instance = it }
            }
    }
}
