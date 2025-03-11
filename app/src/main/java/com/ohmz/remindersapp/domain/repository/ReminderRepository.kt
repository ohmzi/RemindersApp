package com.ohmz.remindersapp.domain.repository

import com.ohmz.remindersapp.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    /**
     * Get all reminders as a Flow
     */
    fun getReminders(): Flow<List<Reminder>>

    /**
     * Get reminder by ID
     */
    suspend fun getReminderById(id: Int): Reminder?

    /**
     * Add a new reminder
     */
    suspend fun addReminder(reminder: Reminder): Long

    /**
     * Update an existing reminder
     */
    suspend fun updateReminder(reminder: Reminder)

    /**
     * Delete a reminder
     */
    suspend fun deleteReminder(reminder: Reminder)

    /**
     * Get reminders by completion status
     */
    fun getRemindersByCompletionStatus(isCompleted: Boolean): Flow<List<Reminder>>

    /**
     * Get reminders by favorite status
     */
    fun getFavoriteReminders(): Flow<List<Reminder>>

    /**
     * Get reminders with due date
     */
    fun getScheduledReminders(): Flow<List<Reminder>>

    /**
     * Get reminders due today
     */
    fun getTodayReminders(): Flow<List<Reminder>>
}
