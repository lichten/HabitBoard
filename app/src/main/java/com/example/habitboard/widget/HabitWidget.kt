package com.example.habitboard.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
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
import com.example.habitboard.R
import com.example.habitboard.data.model.Habit
import com.example.habitboard.data.repository.HabitRepository
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val BgColor = Color(0xFFFDE182)
private val OnBgColor = Color(0xFF1A0A00)
private val PrimaryColor = Color(0xFF5C3310)
private val SubduedColor = Color(0xFF7A5520)

class HabitWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = HabitRepository(context)
        val today = LocalDate.now()
        val habits = repository.getHabitsSync()
        val records = repository.getRecordsForDateSync(today)
        val recordMap = records.associate { it.habitId to it.isDone }
        val lastUpdated = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

        provideContent {
            WidgetContent(habits = habits, recordMap = recordMap, lastUpdated = lastUpdated)
        }
    }
}

@Composable
private fun WidgetContent(habits: List<Habit>, recordMap: Map<Int, Boolean>, lastUpdated: String) {
    val context = LocalContext.current
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
            Row(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = context.getString(R.string.widget_title),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ColorProvider(OnBgColor)
                    )
                )
                Spacer(modifier = GlanceModifier.width(6.dp))
                Text(
                    text = context.getString(R.string.widget_last_updated, lastUpdated),
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = ColorProvider(SubduedColor)
                    )
                )
            }
            Text(
                text = context.getString(R.string.widget_count, doneCount, totalCount),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ColorProvider(PrimaryColor)
                )
            )
        }
        Spacer(modifier = GlanceModifier.height(8.dp))
        habits.forEach { habit ->
            val isDone = recordMap[habit.id] ?: false
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isDone) context.getString(R.string.widget_done_marker)
                           else context.getString(R.string.widget_undone_marker),
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
