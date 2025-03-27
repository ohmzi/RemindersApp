package com.ohmz.remindersapp.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ohmz.remindersapp.data.local.entity.ReminderEntity
import com.ohmz.remindersapp.domain.model.Priority
import com.ohmz.remindersapp.domain.model.Reminder
import java.util.Date

private val gson = Gson()

fun ReminderEntity.toDomainModel(): Reminder {
    // Parse the AI suggestions from a JSON string to a List<String>
    val suggestionsList = if (!aiSuggestions.isNullOrBlank()) {
        try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson<List<String>>(aiSuggestions, type)
        } catch (e: Exception) {
            emptyList()
        }
    } else {
        emptyList()
    }
    
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
        imageUri = imageUri,
        hasAiSuggestions = hasAiSuggestions,
        aiSuggestions = suggestionsList
    )
}

fun Reminder.toEntity(): ReminderEntity {
    // Convert the AI suggestions from a List<String> to a JSON string
    val suggestionsJson = if (aiSuggestions.isNotEmpty()) {
        gson.toJson(aiSuggestions)
    } else {
        null
    }
    
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
        imageUri = imageUri,
        hasAiSuggestions = hasAiSuggestions,
        aiSuggestions = suggestionsJson
    )
}

fun List<ReminderEntity>.toDomainModel(): List<Reminder> {
    return map { it.toDomainModel() }
}

fun List<Reminder>.toEntity(): List<ReminderEntity> {
    return map { it.toEntity() }
}
