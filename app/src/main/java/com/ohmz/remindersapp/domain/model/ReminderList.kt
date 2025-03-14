package com.ohmz.remindersapp.domain.model

import androidx.compose.ui.graphics.Color
import com.ohmz.remindersapp.presentation.common.theme.IOSColors

/**
 * Domain model for a ReminderList
 * This class is used within the business logic and presentation layer
 */
data class ReminderList(
    val id: Int = 0,
    val name: String,
    val color: String = "#007AFF", // Default iOS blue color
    val isDefault: Boolean = false
)