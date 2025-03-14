package com.ohmz.remindersapp.data.local.entity

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ohmz.remindersapp.presentation.common.theme.IOSColors

/**
 * Database entity for the reminder_lists table
 * This class represents the structure of the reminder_lists table in Room
 */
@Entity(tableName = "reminder_lists")
data class ReminderListEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val name: String,
    val color: Color = IOSColors.Blue, // Default iOS blue color from IOSColors
    val isDefault: Boolean = false  // Flag to indicate if this is a default list
)