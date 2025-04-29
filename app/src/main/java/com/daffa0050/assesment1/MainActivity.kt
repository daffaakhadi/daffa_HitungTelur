package com.daffa0050.assesment1

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.daffa0050.assesment1.screen.ListPemesananScreen
import com.daffa0050.assesment1.screen.MainScreen
import com.daffa0050.assesment1.screen.NewsScreen
import com.daffa0050.assesment1.ui.theme.Assesment1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AssesmentApp()
        }
    }
}

@Composable
fun AssesmentApp() {
    Assesment1Theme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "home") {
            composable("home") { MainScreen(navController) }
            composable("news") { NewsScreen(navController) }
            composable("main") { MainScreen(navController) }
            composable("list_pemesanan") { ListPemesananScreen(navController) }
        }
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun Assesment1AppPreviewLight() {
    AssesmentApp()
}

@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
fun Assesment1AppPreviewDark() {
    AssesmentApp()
}
