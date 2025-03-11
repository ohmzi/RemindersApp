package com.ohmz.remindersapp.domain.usecase

import com.ohmz.remindersapp.domain.model.Reminder
import com.ohmz.remindersapp.domain.repository.ReminderRepository
import javax.inject.Inject

class AddReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(reminder: Reminder): Long {
        return repository.addReminder(reminder)
    }
}
