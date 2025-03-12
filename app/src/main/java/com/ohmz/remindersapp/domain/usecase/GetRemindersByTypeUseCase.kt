package com.ohmz.remindersapp.domain.usecase

import com.ohmz.remindersapp.domain.model.Reminder
import com.ohmz.remindersapp.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRemindersByTypeUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    fun getToday(): Flow<List<Reminder>> {
        return repository.getTodayReminders()
    }

    fun getScheduled(): Flow<List<Reminder>> {
        return repository.getScheduledReminders()
    }

    fun getAll(): Flow<List<Reminder>> {
        return repository.getReminders()
    }

    fun getFavourite(): Flow<List<Reminder>> {
        return repository.getFavoriteReminders()
    }

    fun getCompleted(): Flow<List<Reminder>> {
        return repository.getRemindersByCompletionStatus(true)
    }
}
