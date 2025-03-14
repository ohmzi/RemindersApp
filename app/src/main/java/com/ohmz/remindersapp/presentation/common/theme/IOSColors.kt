package com.ohmz.remindersapp.presentation.common.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Comprehensive color palette for the RemindersApp, organized by purpose
 */
object AppColors {
    // Primary colors used across the app
    object Primary {
        // Light mode values
        val Blue = Color(0xFF007AFF)
        val Green = Color(0xFF34C759) 
        val Red = Color(0xFFFF3B30)
        val Orange = Color(0xFFFF9500)
        val Yellow = Color(0xFFFFCC00)
        val Black = Color(0xFF000000)
        
        // Dark mode alternatives
        val BlueDark = Color(0xFF0A84FF)
        val GreenDark = Color(0xFF30D158)
        val RedDark = Color(0xFFFF453A)
        val OrangeDark = Color(0xFFFF9F0A)
        val YellowDark = Color(0xFFFFD60A)
    }
    
    // Background colors
    object Background {
        // Light mode
        val LightGray = Color(0xFFF2F2F7)  // iOS background gray
        val White = Color(0xFFFFFFFF)
        
        // Dark mode
        val Black = Color(0xFF000000)
        val DarkGray = Color(0xFF73737A)
        val DarkGray2 = Color(0xFF2C2C2E)
    }
    
    // Grayscale colors for UI elements
    object Gray {
        // Light mode
        val Gray = Color(0xFF8E8E93)       // Standard gray
        val Gray2 = Color(0xFFAEAEB2)
        val Gray3 = Color(0xFFC7C7CC)
        val Gray4 = Color(0xFFD1D1D6)
        val Gray5 = Color(0xFFE5E5EA)
        val Gray6 = Color(0xFFF2F2F7)
        
        // Dark mode
        val GrayDark = Color(0xFF8E8E93)
        val GrayDark2 = Color(0xFF636366)
        val GrayDark3 = Color(0xFF48484A)
        val GrayDark4 = Color(0xFF3A3A3C)
        val GrayDark5 = Color(0xFF2C2C2E)
        val GrayDark6 = Color(0xFF1C1C1E)
    }
    
    // Text colors
    object Text {
        // Light mode
        val Primary = Color(0xFF000000)
        val Secondary = Color(0x993C3C43)
        val Tertiary = Color(0x4C3C3C43)
        
        // Dark mode
        val PrimaryDark = Color(0xFFFFFFFF)
        val SecondaryDark = Color(0x99EBEBF5)
        val TertiaryDark = Color(0x4CEBEBF5)
    }
    
    // Reminder category colors
    object ReminderType {
        // Light mode
        val Today = Primary.Blue
        val Scheduled = Primary.Orange
        val All = Primary.Black
        val Favorite = Primary.Red
        val Completed = Gray.Gray
        
        // Dark mode
        val TodayDark = Primary.BlueDark
        val ScheduledDark = Primary.OrangeDark
        val AllDark = Color.White
        val FavoriteDark = Primary.RedDark
        val CompletedDark = Gray.GrayDark
    }
}

/**
 * Globally accessible iOS-style color palette that can be used outside the composition
 * These are designed for direct color references (static contexts)
 */
object IOSColors {
    // Primary colors
    val Blue = AppColors.Primary.Blue
    val Green = AppColors.Primary.Green
    val Red = AppColors.Primary.Red
    val Orange = AppColors.Primary.Orange
    val Yellow = AppColors.Primary.Yellow
    val Black = AppColors.Primary.Black
    
    // Dark mode primary colors
    val BlueDark = AppColors.Primary.BlueDark
    val GreenDark = AppColors.Primary.GreenDark
    val RedDark = AppColors.Primary.RedDark
    val OrangeDark = AppColors.Primary.OrangeDark
    val YellowDark = AppColors.Primary.YellowDark
    
    // Gray scales
    val Gray = AppColors.Gray.Gray
    val Gray2 = AppColors.Gray.Gray2
    val Gray3 = AppColors.Gray.Gray3
    val Gray4 = AppColors.Gray.Gray4
    val Gray5 = AppColors.Gray.Gray5
    val Gray6 = AppColors.Gray.Gray6
    
    // Dark mode gray scales
    val GrayDark = AppColors.Gray.GrayDark
    val GrayDark2 = AppColors.Gray.GrayDark2
    val GrayDark3 = AppColors.Gray.GrayDark3
    val GrayDark4 = AppColors.Gray.GrayDark4
    val GrayDark5 = AppColors.Gray.GrayDark5
    val GrayDark6 = AppColors.Gray.GrayDark6
    
    // Background colors
    val LightGray = AppColors.Background.LightGray
    val White = AppColors.Background.White
    val DarkGray = AppColors.Background.DarkGray
    val DarkGray2 = AppColors.Background.DarkGray2
    
    // Transparent colors
    val Transparent = Color.Transparent
    
    // Standard colors (for compatibility)
    val StandardWhite = Color.White
    val StandardBlack = Color.Black
    
    // Contextual colors
    val OverdueRed = AppColors.Primary.Red
    val TodayBlue = AppColors.Primary.Blue
    val ScheduledOrange = AppColors.Primary.Orange
    val FavoriteRed = AppColors.Primary.Red
    val CompletedGray = AppColors.Gray.Gray
    
    // Status colors
    val Success = Green
    val Error = Red
    val Warning = Orange
    val Info = Blue
    
    // Additional UI colors
    val DisabledGray = Gray4
    val DividerColor = Gray5
    val ShadowColor = Color(0x29000000) // 16% opacity black
    val OverlayColor = Color(0x99000000) // 60% opacity black
    
    // Button and Control colors
    val ButtonGray = Color(0xFFE0E0E0)
    val ButtonGrayBorder = Color(0xFFBDBDBD)
    val ButtonActiveBackground = Blue.copy(alpha = 0.15f)
    val DarkGrayText = Color(0xFF202020)
    
    /**
     * Get appropriate color version for the current theme
     * Note: This should only be used within a @Composable function
     */
    @Composable
    fun getThemeAwareColor(lightColor: Color, darkColor: Color): Color {
        return if (isSystemInDarkTheme()) darkColor else lightColor
    }
}

// Composition local to provide app-specific colors beyond MaterialTheme
val LocalAppColors = staticCompositionLocalOf {
    AppColorsScheme(isDark = false) // Default to light theme
}

/**
 * Custom color scheme holder for app-specific colors that aren't covered by Material Theme
 */
data class AppColorsScheme(
    val isDark: Boolean,
    // Category colors
    val todayColor: Color = if (isDark) AppColors.ReminderType.TodayDark else AppColors.ReminderType.Today,
    val scheduledColor: Color = if (isDark) AppColors.ReminderType.ScheduledDark else AppColors.ReminderType.Scheduled,
    val allColor: Color = if (isDark) AppColors.ReminderType.AllDark else AppColors.ReminderType.All,
    val favoriteColor: Color = if (isDark) AppColors.ReminderType.FavoriteDark else AppColors.ReminderType.Favorite,
    val completedColor: Color = if (isDark) AppColors.ReminderType.CompletedDark else AppColors.ReminderType.Completed,
    
    // Background colors
    val mainBackground: Color = if (isDark) AppColors.Background.Black else AppColors.Background.LightGray,
    val cardBackground: Color = if (isDark) AppColors.Background.DarkGray else AppColors.Background.White,
    val secondaryBackground: Color = if (isDark) AppColors.Background.DarkGray else AppColors.Background.White,
    
    // Text colors
    val primaryText: Color = if (isDark) AppColors.Text.PrimaryDark else AppColors.Text.Primary,
    val secondaryText: Color = if (isDark) AppColors.Text.SecondaryDark else AppColors.Text.Secondary,
    
    // Functional colors
    val overdueColor: Color = if (isDark) AppColors.Primary.RedDark else AppColors.Primary.Red
)

// Enhanced iOS light theme
val IOSLightColorScheme = lightColorScheme(
    primary = AppColors.Primary.Blue,
    onPrimary = IOSColors.White,
    secondary = AppColors.Primary.Green,
    onSecondary = IOSColors.White,
    tertiary = AppColors.Primary.Orange,
    onTertiary = IOSColors.White,
    background = AppColors.Background.LightGray,
    onBackground = AppColors.Text.Primary,
    surface = AppColors.Background.White,
    onSurface = AppColors.Text.Primary,
    error = AppColors.Primary.Red,
    onError = IOSColors.White
)

// Enhanced iOS dark theme with proper dark colors
val IOSDarkColorScheme = darkColorScheme(
    primary = AppColors.Primary.BlueDark,
    onPrimary = IOSColors.White,
    secondary = AppColors.Primary.GreenDark,
    onSecondary = IOSColors.Black,
    tertiary = AppColors.Primary.OrangeDark,
    onTertiary = IOSColors.Black,
    background = AppColors.Background.Black,
    onBackground = AppColors.Text.PrimaryDark,
    surface = AppColors.Background.DarkGray,
    onSurface = AppColors.Text.PrimaryDark,
    error = AppColors.Primary.RedDark,
    onError = IOSColors.Black
)

/**
 * Provides the app's custom colors through CompositionLocal
 */
@Composable
fun ProvideAppColors(
    isDark: Boolean,
    content: @Composable () -> Unit
) {
    val appColors = AppColorsScheme(isDark = isDark)
    CompositionLocalProvider(LocalAppColors provides appColors) {
        content()
    }
}

/**
 * Access the app's custom colors within a composable
 */
val AppTheme: AppColorsScheme
    @Composable
    get() = LocalAppColors.current