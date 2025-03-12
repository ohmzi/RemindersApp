package com.ohmz.remindersapp.domain.repository

import com.ohmz.remindersapp.domain.model.ReminderList
import kotlinx.coroutines.flow.Flow

interface ReminderListRepository {
    /**
     * Gets all reminder lists
     */
    fun getAllLists(): Flow<List<ReminderList>>
    
    /**
     * Gets a reminder list by its ID
     */
    suspend fun getListById(id: Int): ReminderList?
    
    /**
     * Adds a new reminder list and returns its ID
     */
    suspend fun addList(list: ReminderList): Long
    
    /**
     * Updates an existing reminder list
     */
    suspend fun updateList(list: ReminderList)
    
    /**
     * Deletes a reminder list
     */
    suspend fun deleteList(list: ReminderList)
    
    /**
     * Gets the default list if one exists, otherwise returns null
     */
    suspend fun getOrCreateDefaultList(): ReminderList?
}