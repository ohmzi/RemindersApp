package com.ohmz.remindersapp.ui.navigations

import com.ohmz.remindersapp.ui.reminderScreen.reminderScreen.AddReminderScreen
import com.ohmz.remindersapp.ui.reminderScreen.reminderScreen.ReminderListScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

sealed class Screen(val route: String) {
    object List : Screen("list")
    object Add : Screen("add")
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.List.route
    ) {
        composable(Screen.List.route) {
            ReminderListScreen()
        }
        composable(Screen.Add.route) {
            AddReminderScreen(
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}
