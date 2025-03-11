package com.ohmz.remindersapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ohmz.remindersapp.domain.model.ReminderType
import com.ohmz.remindersapp.presentation.reminder.detail.ReminderFilteredListScreen
import com.ohmz.remindersapp.presentation.reminder.main.ReminderMainScreen

/**
 * Enum representing all possible navigation destinations in the app
 */
sealed class Screen(val route: String) {
    object ReminderMain : Screen("reminder_main")
    object ReminderFilteredList : Screen("reminder_filtered_list/{type}") {
        fun createRoute(type: ReminderType): String {
            return "reminder_filtered_list/${type.name}"
        }
    }

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
    navController: NavHostController,
    startDestination: String = Screen.ReminderMain.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Main Screen with category buttons
        composable(route = Screen.ReminderMain.route) {
            ReminderMainScreen(
                navigateToAddReminder = {
                    // This is just a placeholder - we now use the bottom sheet directly
                },
                navigateToFilteredList = { reminderType ->
                    navController.navigate(Screen.ReminderFilteredList.createRoute(reminderType))
                }
            )
        }

        // Filtered List Screen showing specific reminders
        composable(
            route = Screen.ReminderFilteredList.route,
            arguments = listOf(
                navArgument("type") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val typeString = backStackEntry.arguments?.getString("type") ?: ReminderType.ALL.name
            val reminderType = ReminderType.valueOf(typeString)

            ReminderFilteredListScreen(
                reminderType = reminderType,
                onNavigateBack = {
                    navController.popBackStack()
                },
                navigateToAddReminder = {
                    // This is just a placeholder - we now use the bottom sheet directly
                }
            )
        }
    }
}