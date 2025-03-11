package com.ohmz.remindersapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ohmz.remindersapp.presentation.reminder.add.AddReminderScreen
import com.ohmz.remindersapp.presentation.reminder.list.ReminderListScreen

/**
 * Enum representing all possible navigation destinations in the app
 */
sealed class Screen(val route: String) {
    object ReminderList : Screen("reminder_list")
    object AddReminder : Screen("add_reminder")

    // Helper function to create route with arguments
    fun createRoute(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}

/**
 * Main navigation component for the app
 */
@Composable
fun AppNavHost(
    navController: NavHostController, startDestination: String = Screen.ReminderList.route
) {
    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        // Reminder List Screen
        composable(route = Screen.ReminderList.route) {
            ReminderListScreen(navigateToAddReminder = {
                navController.navigate(Screen.AddReminder.route)
            })
        }

        // Add Reminder Screen
        composable(route = Screen.AddReminder.route) {
            val viewModel: com.ohmz.remindersapp.presentation.reminder.add.AddReminderViewModel =
                androidx.hilt.navigation.compose.hiltViewModel()
            AddReminderScreen(
                onNavigateBack = {
                    viewModel.resetState()
                    navController.popBackStack()
                }, viewModel = viewModel
            )
        }
    }
}