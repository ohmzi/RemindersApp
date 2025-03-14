package com.ohmz.remindersapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

/**
 * Database entity for the reminder_lists table
 * This class represents the structure of the reminder_lists table in Room
 */
@Entity(tableName = "reminder_lists")
data class ReminderListEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val name: String,
    
    // Store color as a hex string like "#007AFF" for iOS blue
    @ColumnInfo(name = "color")
    val colorHex: String = "#007AFF", // Default iOS blue color hex
    
    val isDefault: Boolean = false  // Flag to indicate if this is a default list
)