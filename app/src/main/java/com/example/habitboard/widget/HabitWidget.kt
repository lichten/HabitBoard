package com.example.habitboard.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.habitboard.MainActivity
import com.example.habitboard.data.model.Habit
import com.example.habitboard.data.repository.HabitRepository
import java.time.LocalDate

private val BgColor = Color(0xFF1C1B1F)
private val OnBgColor = Color(0xFFE6E1E5)
private val PrimaryColor = Color(0xFFD0BCFF)
private val SubduedColor = Color(0xFF938F99)

class HabitWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = HabitRepository(context)
        val today = LocalDate.now()
        val habits = repository.getHabitsSync()
        val records = repository.getRecordsForDateSync(today)
        val recordMap = records.associate { it.habitId to it.isDone }

        provideContent {
            WidgetContent(habits = habits, recordMap = recordMap)
        }
    }
}

@Composable
private fun WidgetContent(habits: List<Habit>, recordMap: Map<Int, Boolean>) {
    val doneCount = recordMap.values.count { it }
    val totalCount = habits.size

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(BgColor)
            .clickable(actionStartActivity<MainActivity>())
            .padding(12.dp)
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "今日の習慣",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ColorProvider(OnBgColor)
                ),
                modifier = GlanceModifier.defaultWeight()
            )
            Text(
                text = "$doneCount / $totalCount",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ColorProvider(PrimaryColor)
                )
            )
        }
        Spacer(modifier = GlanceModifier.height(8.dp))
        habits.take(5).forEach { habit ->
            val isDone = recordMap[habit.id] ?: false
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isDone) "✓ " else "ー ",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = ColorProvider(if (isDone) PrimaryColor else SubduedColor)
                    )
                )
                Text(
                    text = habit.name,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = ColorProvider(if (isDone) OnBgColor else SubduedColor)
                    )
                )
            }
        }
    }
}
