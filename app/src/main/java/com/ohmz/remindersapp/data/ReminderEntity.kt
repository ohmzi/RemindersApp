package com.ohmz.remindersapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val notes: String?,
    val dueDate: Long?,       // e.g., store timestamps
    val isCompleted: Boolean,
    /*

        val reminderDate: Long?,    // Separate date for the reminder (timestamp representing the date)
        val reminderTime: Long?,    // Separate time for the reminder (timestamp representing the time or offset)
        val tag: String?,           // Tag, e.g., "Work", "Personal"
        val isFavourite: Boolean,   // Flag to mark as favourite
        val priority: Priority,     // Using the enum
        val image: String?,         // Image URI or file path as String
        val url: String?            // Associated URL as String
     */

)
