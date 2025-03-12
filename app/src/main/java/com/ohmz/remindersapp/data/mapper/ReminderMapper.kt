package com.ohmz.remindersapp.data.mapper

import com.ohmz.remindersapp.data.local.entity.ReminderEntity
import com.ohmz.remindersapp.domain.model.Priority
import com.ohmz.remindersapp.domain.model.Reminder
import java.util.*

fun ReminderEntity.toDomainModel(): Reminder {
    return Reminder(
        id = id,
        title = title,
        notes = notes,
        dueDate = dueDate?.let { Date(it) },
        isCompleted = isCompleted,
        isFavorite = isFavorite,
        priority = try {
            Priority.valueOf(priority)
        } catch (e: IllegalArgumentException) {
            Priority.MEDIUM
        },
        tags = tags?.split(",")?.map { it.trim() } ?: emptyList(),
        listId = listId,
        imageUri = imageUri
    )
}

fun Reminder.toEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        title = title,
        notes = notes,
        dueDate = dueDate?.time,
        isCompleted = isCompleted,
        isFavorite = isFavorite,
        priority = priority.name,
        tags = if (tags.isEmpty()) null else tags.joinToString(","),
        listId = listId,
        imageUri = imageUri
    )
}

fun List<ReminderEntity>.toDomainModel(): List<Reminder> {
    return map { it.toDomainModel() }
}

fun List<Reminder>.toEntity(): List<ReminderEntity> {
    return map { it.toEntity() }
}
