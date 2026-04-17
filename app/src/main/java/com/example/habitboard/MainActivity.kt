package com.example.habitboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.habitboard.ui.main.MainScreen
import com.example.habitboard.ui.manage.ManageScreen
import com.example.habitboard.ui.theme.HabitBoardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitBoardTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(
                            onNavigateToManage = { navController.navigate("manage") }
                        )
                    }
                    composable("manage") {
                        ManageScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
