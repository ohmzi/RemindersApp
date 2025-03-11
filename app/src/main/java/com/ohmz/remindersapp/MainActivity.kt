package com.ohmz.remindersapp

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.ohmz.remindersapp.presentation.common.theme.RemindersAppTheme
import com.ohmz.remindersapp.presentation.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point for the app
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Make system bars (status and navigation) transparent and draw content behind them
        enableEdgeToEdge()
        
        // Make the status and navigation bars blend with the app background
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            RemindersAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavHost(navController = navController)
                }
            }
        }
    }
}