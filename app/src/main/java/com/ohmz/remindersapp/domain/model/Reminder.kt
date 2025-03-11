package com.ohmz.remindersapp.domain.model

import java.util.Date

/**
 * Domain model for a Reminder
 * This class is used within the business logic and presentation layer
 */
data class Reminder(
    val id: Int = 0,
    val title: String,
    val notes: String? = null,
    val dueDate: Date? = null,      // Using Date for domain model
    val isCompleted: Boolean = false,
    val isFavorite: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val tags: List<String> = emptyList(),
    val location: String? = null,
    val imageUri: String? = null
)