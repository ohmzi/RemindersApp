package com.ohmz.remindersapp.presentation.common.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Enhanced iOS-style colors
object IOSColors {
    // Primary colors
    val Blue = Color(0xFF007AFF)
    val Green = Color(0xFF34C759)
    val Red = Color(0xFFFF3B30)
    val Orange = Color(0xFFFF9500)
    val Yellow = Color(0xFFFFCC00)
    val Purple = Color(0xFF5856D6)
    val Pink = Color(0xFFFF2D55)

    // Grays
    val Gray = Color(0xFF8E8E93)
    val Gray2 = Color(0xFFAEAEB2)
    val Gray3 = Color(0xFFC7C7CC)
    val Gray4 = Color(0xFFD1D1D6)
    val Gray5 = Color(0xFFE5E5EA)
    val Gray6 = Color(0xFFF2F2F7)

    // Text colors
    val LabelLight = Color(0xFF000000)
    val SecondaryLabelLight = Color(0x993C3C43)
    val TertiaryLabelLight = Color(0x4C3C3C43)
    val QuaternaryLabelLight = Color(0x2E3C3C43)

    // Background
    val SystemBackground = Color(0xFFF2F2F7)
    val SecondarySystemBackground = Color(0xFFFFFFFF)
    val TertiarySystemBackground = Color(0xFFE5E5EA)
}

// Enhanced iOS light theme
val IOSLightColorScheme = lightColorScheme(
    primary = IOSColors.Blue,
    onPrimary = Color.White,
    secondary = IOSColors.Green,
    onSecondary = Color.White,
    tertiary = IOSColors.Orange,
    onTertiary = Color.White,
    background = IOSColors.SystemBackground,
    onBackground = IOSColors.LabelLight,
    surface = IOSColors.SecondarySystemBackground,
    onSurface = IOSColors.LabelLight,
    error = IOSColors.Red,
    onError = Color.White
)

// Enhanced iOS dark theme (if needed)
val IOSDarkColorScheme = darkColorScheme(
    primary = IOSColors.Blue,
    onPrimary = Color.White,
    secondary = IOSColors.Green,
    onSecondary = Color.White,
    tertiary = IOSColors.Orange,
    onTertiary = Color.White,
    // Other dark theme colors would be defined here
)