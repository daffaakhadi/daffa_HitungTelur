package com.daffa0050.assesment1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.daffa0050.assesment1.screen.MainScreen
import com.daffa0050.assesment1.screen.NewsScreen
import com.daffa0050.assesment1.ui.theme.Assesment1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assesment1Theme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { MainScreen(navController) }
                    composable("news") { NewsScreen(navController) } // Tambahkan ini
                }
            }
        }
    }
}
