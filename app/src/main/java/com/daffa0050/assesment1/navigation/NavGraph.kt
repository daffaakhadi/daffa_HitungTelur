package com.daffa0050.assesment1.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.daffa0050.assesment1.model.AuthViewModel
import com.daffa0050.assesment1.screen.*
import com.daffa0050.assesment1.util.SettingsDataStore

@Composable
fun NavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            val context = androidx.compose.ui.platform.LocalContext.current
            val dataStore = SettingsDataStore(context)
            WelcomeScreen(
                navController = navController,
                context = context,
                dataStore = dataStore
            )
        }

        composable("home") { MainScreen(navController) }
        composable("main") { MainScreen(navController) }
        composable("news") { NewsScreen(navController) }
        composable("info") { InfoScreen(navController) }
        composable("keuangan") { KeuanganScreen(navController) }
        composable("login") { LoginScreen(navController, authViewModel) }
        composable("register") { RegisterScreen(navController, authViewModel) }
        composable("list_pemesanan") { ListPemesananScreen(navController) }
        composable(
            "edit_pemesanan/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            EditPemesananScreen(navController = navController, id = id)
        }
    }
}
