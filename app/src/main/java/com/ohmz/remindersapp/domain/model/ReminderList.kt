package com.ohmz.remindersapp.domain.model

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