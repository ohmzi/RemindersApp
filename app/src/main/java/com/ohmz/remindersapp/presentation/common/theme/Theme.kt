package com.ohmz.remindersapp.presentation.common.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Main theme for the RemindersApp that sets up all styling including colors, typography, and dimensions.
 * This theme uses the iOS-inspired colors.
 */
@Composable
fun RemindersAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Set color scheme based on dark/light theme
    val colorScheme = if (darkTheme) {
        IOSDarkColorScheme
    } else {
        IOSLightColorScheme
    }
    
    // Provide the app's custom colors through composition local
    ProvideAppColors(isDark = darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}