package com.example.habitboard

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.habitboard.ui.calendar.CalendarScreen
import com.example.habitboard.ui.editday.EditDayScreen
import com.example.habitboard.ui.main.MainScreen
import com.example.habitboard.ui.manage.ManageScreen
import com.example.habitboard.ui.settings.SettingsScreen
import java.time.LocalDate
import com.example.habitboard.ui.theme.HabitBoardTheme
import androidx.glance.appwidget.updateAll
import com.example.habitboard.widget.HabitWidget
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop called — updating widget")
        lifecycleScope.launch { HabitWidget().updateAll(this@MainActivity) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitBoardTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(
                            onNavigateToManage = { navController.navigate("manage") },
                            onNavigateToCalendar = { navController.navigate("calendar") }
                        )
                    }
                    composable("manage") {
                        ManageScreen(onBack = { navController.popBackStack() })
                    }
                    composable("calendar") {
                        CalendarScreen(
                            onBack = { navController.popBackStack() },
                            onNavigateToSettings = { navController.navigate("settings") },
                            onNavigateToEditDay = { date -> navController.navigate("edit_day/$date") }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(onBack = { navController.popBackStack() })
                    }
                    composable("edit_day/{date}") { backStackEntry ->
                        val date = LocalDate.parse(backStackEntry.arguments?.getString("date"))
                        EditDayScreen(
                            date = date,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
