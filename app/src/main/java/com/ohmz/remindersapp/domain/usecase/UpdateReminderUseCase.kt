package com.ohmz.remindersapp.domain.usecase

import com.ohmz.remindersapp.domain.model.Reminder
import com.ohmz.remindersapp.domain.repository.ReminderRepository
import javax.inject.Inject

class UpdateReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(reminder: Reminder) {
        repository.updateReminder(reminder)
    }
}
