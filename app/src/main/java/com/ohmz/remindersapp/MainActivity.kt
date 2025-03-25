package com.ohmz.remindersapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.ohmz.remindersapp.presentation.common.theme.IOSColors
import com.ohmz.remindersapp.presentation.common.theme.RemindersAppTheme
import com.ohmz.remindersapp.presentation.navigation.AppNavHost
import com.ohmz.remindersapp.util.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main entry point for the app
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var permissionManager: PermissionManager
    
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make system bars (status and navigation) transparent and draw content behind them
        enableEdgeToEdge()

        // Make the status and navigation bars blend with the app background
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Optional: Set status bar color to transparent
        window.statusBarColor = IOSColors.Transparent.value.toInt()
        
        // Register permission launcher
        notificationPermissionLauncher = permissionManager.registerNotificationPermissionLauncher(this) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied. You won't receive reminder alerts.", Toast.LENGTH_LONG).show()
            }
        }
        
        // Check and request notification permission
        if (!permissionManager.hasNotificationPermission()) {
            permissionManager.requestNotificationPermission(notificationPermissionLauncher)
        }
        
        setContent {
            RemindersAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavHost(navController = navController)
                }
            }
        }
    }
}