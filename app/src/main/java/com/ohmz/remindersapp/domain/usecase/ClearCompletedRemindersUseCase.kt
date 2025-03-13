package com.ohmz.remindersapp.domain.usecase

import com.ohmz.remindersapp.domain.model.Reminder
import com.ohmz.remindersapp.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

/**
 * Use case for clearing all completed reminders
 */
class ClearCompletedRemindersUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    /**
     * Clear all completed reminders
     */
    suspend operator fun invoke() {
        val completedReminders = repository.getRemindersByCompletionStatus(true).firstOrNull() ?: emptyList()

        completedReminders.forEach { reminder ->
            repository.deleteReminder(reminder)
        }
    }
}