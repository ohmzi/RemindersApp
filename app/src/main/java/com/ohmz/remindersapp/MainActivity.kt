package com.ohmz.remindersapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.ohmz.remindersapp.ui.navigations.AppNavHost
import com.ohmz.remindersapp.ui.theme.RemindersAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            RemindersAppTheme {
                AppNavHost(navController = navController)
            }
        }
    }
}
