package com.ohmz.remindersapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String?,
    val dueDate: Long?,       // e.g., store timestamps
    val isCompleted: Boolean
)
