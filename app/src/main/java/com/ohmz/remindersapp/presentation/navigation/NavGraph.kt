package com.ohmz.remindersapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ohmz.remindersapp.domain.model.ReminderType
import com.ohmz.remindersapp.presentation.reminder.detail.ReminderFilteredListScreen
import com.ohmz.remindersapp.presentation.reminder.list.ReminderListByListScreen
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

    object ReminderListByList : Screen("reminder_list_by_list/{listId}/{listName}/{listColorInt}") {
        fun createRoute(listId: Int, listName: String, listColor: Color): String {
            // URL encode list name and convert color to integer for the route
            val encodedName = java.net.URLEncoder.encode(listName, "UTF-8")
            val colorInt = listColor.toArgb()
            return "reminder_list_by_list/$listId/$encodedName/$colorInt"
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
    navController: NavHostController, startDestination: String = Screen.ReminderMain.route
) {
    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        // Main Screen with category buttons
        composable(route = Screen.ReminderMain.route) {
            ReminderMainScreen(navigateToAddReminder = {
                // This is just a placeholder - we now use the bottom sheet directly
            }, navigateToFilteredList = { reminderType ->
                navController.navigate(Screen.ReminderFilteredList.createRoute(reminderType))
            }, navController = navController
            )
        }

        // Filtered List Screen showing specific reminders by type
        composable(
            route = Screen.ReminderFilteredList.route, arguments = listOf(navArgument("type") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val typeString = backStackEntry.arguments?.getString("type") ?: ReminderType.ALL.name
            val reminderType = ReminderType.valueOf(typeString)

            ReminderFilteredListScreen(reminderType = reminderType, onNavigateBack = {
                navController.popBackStack()
            }, navigateToAddReminder = {
                // This is just a placeholder - we now use the bottom sheet directly
            })
        }

        // List Screen showing reminders by list
        composable(
            route = Screen.ReminderListByList.route, arguments = listOf(navArgument("listId") {
                type = NavType.IntType
            }, navArgument("listName") {
                type = NavType.StringType
            }, navArgument("listColorInt") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getInt("listId") ?: 0
            val listName = backStackEntry.arguments?.getString("listName") ?: ""
            val colorInt = backStackEntry.arguments?.getInt("listColorInt") ?: com.ohmz.remindersapp.presentation.common.theme.IOSColors.Blue.toArgb()

            // URL decode the list name
            val decodedListName = java.net.URLDecoder.decode(listName, "UTF-8")

            // Convert the integer back to a Color
            val listColor = Color(colorInt)

            ReminderListByListScreen(listId = listId,
                listName = decodedListName,
                listColor = listColor,
                onNavigateBack = {
                    navController.popBackStack()
                })
        }
    }
}