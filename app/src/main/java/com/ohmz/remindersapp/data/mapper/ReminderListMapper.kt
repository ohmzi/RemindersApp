package com.ohmz.remindersapp.data.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.ohmz.remindersapp.data.local.entity.ReminderListEntity
import com.ohmz.remindersapp.domain.model.ReminderList
import com.ohmz.remindersapp.presentation.common.theme.IOSColors

/**
 * Mapper functions to convert between domain model and entity
 */

/**
 * Extension function to convert a ReminderListEntity to a domain ReminderList
 */
fun ReminderListEntity.toDomainModel(): ReminderList {
    // Convert hex color to Color object
    val colorObject = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        // Fall back to IOSColors.Blue if the hex string is invalid
        IOSColors.Blue
    }
    
    return ReminderList(
        id = id,
        name = name,
        color = colorObject,
        isDefault = isDefault
    )
}

/**
 * Extension function to convert a domain ReminderList to a ReminderListEntity
 */
fun ReminderList.toEntity(): ReminderListEntity {
    // Convert Color object to a hex string
    val hexString = when (color) {
        IOSColors.Blue -> "#007AFF"
        IOSColors.Red -> "#FF3B30"
        IOSColors.Green -> "#34C759"
        IOSColors.Orange -> "#FF9500"
        IOSColors.Yellow -> "#FFCC00"
        IOSColors.Gray -> "#8E8E93"
        else -> String.format("#%06X", (0xFFFFFF and color.toArgb()))
    }
    
    return ReminderListEntity(
        id = id,
        name = name,
        colorHex = hexString,
        isDefault = isDefault
    )
}

/**
 * Extension function to convert a list of ReminderListEntity to a list of domain ReminderList
 */
fun List<ReminderListEntity>.toDomainModels(): List<ReminderList> {
    return map { it.toDomainModel() }
}

/**
 * Extension function to convert a list of domain ReminderList to a list of ReminderListEntity
 */
fun List<ReminderList>.toEntities(): List<ReminderListEntity> {
    return map { it.toEntity() }
}