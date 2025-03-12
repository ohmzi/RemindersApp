package com.ohmz.remindersapp.data.mapper

import com.ohmz.remindersapp.data.local.entity.ReminderListEntity
import com.ohmz.remindersapp.domain.model.ReminderList

/**
 * Mapper functions to convert between domain model and entity
 */

/**
 * Extension function to convert a ReminderListEntity to a domain ReminderList
 */
fun ReminderListEntity.toDomainModel(): ReminderList {
    return ReminderList(
        id = id,
        name = name,
        color = color,
        isDefault = isDefault
    )
}

/**
 * Extension function to convert a domain ReminderList to a ReminderListEntity
 */
fun ReminderList.toEntity(): ReminderListEntity {
    return ReminderListEntity(
        id = id,
        name = name,
        color = color,
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