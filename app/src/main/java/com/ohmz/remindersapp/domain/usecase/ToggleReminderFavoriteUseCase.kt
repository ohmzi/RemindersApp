package com.ohmz.remindersapp.domain.usecase

import com.ohmz.remindersapp.domain.model.Reminder
import com.ohmz.remindersapp.domain.repository.ReminderRepository
import javax.inject.Inject

/**
 * Use case for toggling a reminder's favorite status
 */
class ToggleReminderFavoriteUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    /**
     * Toggle the favorite status of a reminder
     */
    suspend operator fun invoke(reminder: Reminder) {
        val updatedReminder = reminder.copy(isFavorite = !reminder.isFavorite)
        repository.updateReminder(updatedReminder)
    }
    
    /**
     * Set a specific favorite status for a reminder
     */
    suspend fun setFavorite(reminder: Reminder, isFavorite: Boolean) {
        if (reminder.isFavorite != isFavorite) {
            val updatedReminder = reminder.copy(isFavorite = isFavorite)
            repository.updateReminder(updatedReminder)
        }
    }
}