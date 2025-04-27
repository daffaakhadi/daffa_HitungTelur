package com.daffa0050.assesment1.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object News : Screen("news_screen")
}
