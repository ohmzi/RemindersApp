package com.ohmz.remindersapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database entity for the reminders table
 * This class represents the structure of the reminders table in Room
 */
@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Basic reminder information
    val title: String,
    val notes: String? = null,
    val dueDate: Long? = null,       // Timestamp in milliseconds
    val isCompleted: Boolean = false,

    // Additional fields to support extra features
    val isFavorite: Boolean = false,
    val priority: String = "MEDIUM", // Store enum as string
    val tags: String? = null,        // Store as comma-separated values
    val location: String? = null,    // Location information as string (could be coordinates or address)
    val imageUri: String? = null     // URI of an associated image
)