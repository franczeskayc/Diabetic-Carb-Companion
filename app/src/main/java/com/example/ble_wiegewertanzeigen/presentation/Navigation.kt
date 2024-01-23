package com.example.ble_wiegewertanzeigen.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.StartScreen.route) {
        composable(Screen.StartScreen.route) {
            StartScreen(navController = navController)
        }

        composable(Screen.WeightShowingScreen.route) {
            WeightShowingScreen()
        }
    }
    
}

sealed class Screen(val route:String) {
    object StartScreen:Screen("start_screen")
    object WeightShowingScreen:Screen("weight_show_screen")
}